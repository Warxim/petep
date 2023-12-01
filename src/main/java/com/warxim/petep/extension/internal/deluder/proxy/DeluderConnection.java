/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2023 Michal Válka
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <https://www.gnu.org/licenses/>.
 */
package com.warxim.petep.extension.internal.deluder.proxy;

import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.pdu.DefaultPdu;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.core.pdu.PduQueue;
import com.warxim.petep.proxy.worker.Proxy;
import com.warxim.petep.util.GsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Deluder connection
 * <p>Simple implementation for Deluder integration.</p>
 */
public final class DeluderConnection implements Connection {
    private final String code;
    private final Proxy proxy;
    private final Socket socket;
    private final PduQueue outQueue;
    private ExecutorService executor;
    private DeluderConnectionInfo info = null;

    /**
     * Deluder connection constructor
     * @param code Unique code of the connection
     * @param proxy Proxy to which the connection belongs
     * @param socket Client socket for the connection
     */
    public DeluderConnection(String code, Proxy proxy, Socket socket) {
        this.code = code;
        this.proxy = proxy;
        this.socket = socket;
        this.outQueue = new PduQueue();
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public Proxy getProxy() {
        return proxy;
    }

    @Override
    public String toString() {
        if (info != null) {
            return info.getName();
        }

        return "Deluder connection "
                + code
                + " (" + socket.getInetAddress().getHostAddress() + ":"  + socket.getPort() + ")";
    }

    @Override
    public boolean start() {
        Logger.getGlobal().info(() -> String.format("Starting Deluder connection with code '%s'...", code));

        executor = Executors.newFixedThreadPool(2);

        executor.execute(this::write);
        executor.execute(this::read);
        executor.execute(this::stop);

        Logger.getGlobal().info(() -> String.format("Deluder connection with code '%s' started.", code));

        return true;
    }

    @Override
    public void stop() {
        Logger.getGlobal().info(() -> String.format("Stopping Deluder connection with code '%s'...", code));

        // Shutdown threads
        executor.shutdownNow();

        // Close socket
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Deluder connection exception - IO exception!", e);
        }

        // Remove connection from connection manager
        proxy.getConnectionManager().remove(this);

        Logger.getGlobal().info(() -> String.format("Deluder connection with code '%s' stopped.", code));
    }

    @Override
    public void send(PDU pdu) {
        outQueue.add(pdu);
    }

    @Override
    public boolean supports(PDU pdu) {
        return proxy.supports(pdu);
    }

    /**
     * Writes PDUs to Deluder connection after it gets processed in PETEP
     */
    private void write() {
        // PDU
        PDU pdu;

        try (var out = socket.getOutputStream()) {
            // Read bytes to buffer and send it to out stream.
            while ((pdu = outQueue.take()) != null) {
                // Write type
                if (pdu.getDestination() == PduDestination.SERVER) {
                    out.write(DeluderMessageType.DATA_C2S.getValue());
                } else {
                    out.write(DeluderMessageType.DATA_S2C.getValue());
                }

                // Write length
                out.write(ByteBuffer.allocate(4).putInt(pdu.getSize()).array());

                // Write data
                out.write(pdu.getBuffer(), 0, pdu.getSize());
            }
        } catch (IOException e) {
            // Closed socket
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Reads PDUs from Deluder connection and lets it get processed in PETEP
     */
    private void read() {
        int size;
        byte[] buffer;
        var charset = getConfig().getCharset();
        byte[] lengthBuffer = new byte[4];

        try (var in = socket.getInputStream()) {
            while (true) {
                // Read type
                var typeByte = in.read();
                if (typeByte == -1) {
                    break;
                }
                var maybeMessageType = DeluderMessageType.valueOf(typeByte);
                if (maybeMessageType.isEmpty()) {
                    Logger.getGlobal().log(
                            Level.WARNING,
                            String.format("Message type byte (%s) is not known!", typeByte)
                    );
                    break;
                }
                var messageType = maybeMessageType.get();

                // Read length
                fillBufferOrThrow(in, lengthBuffer);
                size = ByteBuffer.wrap(lengthBuffer).getInt();

                // Read data
                buffer = new byte[size];
                fillBufferOrThrow(in, buffer);

                // Handle connection info
                if (messageType == DeluderMessageType.CONNECTION_INFO) {
                    var infoJson = new String(buffer, 0, size);
                    info = GsonUtils.getGson().fromJson(infoJson, DeluderConnectionInfo.class);
                    continue;
                }

                // Handle DATA message
                // Create PDU from buffer
                var destination = messageType == DeluderMessageType.DATA_C2S
                        ? PduDestination.SERVER
                        : PduDestination.CLIENT;
                var pdu = new DefaultPdu(proxy, this, destination, buffer, size, charset);

                // Process PDU in PETEP.
                proxy.getHelper().processPdu(pdu);
            }
        } catch (IOException e) {
            // Closed
        }
    }

    /**
     * Tries to fill the whole buffer from given input stream
     */
    private void fillBufferOrThrow(InputStream in, byte[] buffer) throws IOException {
        int read = in.readNBytes(buffer, 0, buffer.length);
        if (read != buffer.length) {
            throw new IOException("Could not read the expected number of bytes!");
        }
    }

    /**
     * Obtains Deluder config from proxy
     */
    private DeluderConfig getConfig() {
        return ((DeluderProxy) proxy).getConfig();
    }
}

