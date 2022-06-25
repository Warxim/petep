/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2021 Michal Válka
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
package com.warxim.petep.extension.internal.udp.proxy;

import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.core.pdu.PduQueue;
import com.warxim.petep.proxy.worker.Proxy;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pseudo UDP connection (represents client with certain IP and port).
 * <p>Uses 3 threads for processing input/output:</p>
 * <ul>
 *     <li>Write to server</li>
 *     <li>Write to client</li>
 *     <li>Read from server</li>
 * </ul>
 * <p>(Read from client is done by UdpProxy.)</p>
 */
public class UdpConnection extends Connection {
    private final InetAddress clientAddress;
    private final int clientPort;
    private final DatagramSocket socketClient;
    private DatagramSocket socketServer;
    protected ExecutorService executor;

    /**
     * Constructs UDP connection.
     * @param code Unique code of the connection
     * @param proxy Proxy to which the connection belongs
     * @param clientAddress Address of the client
     * @param clientPort Port on the client
     * @param socketClient Datagram socket of client
     */
    public UdpConnection(String code, Proxy proxy, InetAddress clientAddress, int clientPort, DatagramSocket socketClient) {
        super(code, proxy);
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
        this.socketClient = socketClient;
    }

    @Override
    public boolean start() {
        try {
            executor = Executors.newFixedThreadPool(3);

            var config = ((UdpProxy) proxy).getConfig();
            var serverAddress = InetAddress.getByName(config.getTargetIP());
            var serverPort = config.getTargetPort();
            socketServer = new DatagramSocket();

            // Write to server
            executor.execute(() -> doSend(
                    queueC2S,
                    socketServer,
                    serverAddress,
                    serverPort
            ));

            // Write to client
            executor.execute(() -> doSend(
                    queueS2C,
                    socketClient,
                    clientAddress,
                    clientPort
            ));

            // Read from server
            executor.execute(this::doReadFromServer);

            // Automatic cleanup
            executor.execute(this::cleanup);

            return true;
        } catch (UnknownHostException e) {
            Logger.getGlobal().log(Level.SEVERE, "UDP unknown host exception thrown.", e);
        } catch (SocketException e) {
            Logger.getGlobal().log(Level.SEVERE, "UDP socket exception thrown.", e);
        }

        executor.shutdownNow();

        return false;
    }

    @Override
    public void stop() {
        Logger.getGlobal().info(() -> String.format("Stopping UDP connection with code '%s'...", code));

        executor.shutdownNow();

        if (socketServer != null) {
            socketServer.close();
        }

        proxy.getConnectionManager().remove(this);

        Logger.getGlobal().info(() -> String.format("UDP connection with code '%s' stopped.", code));
    }

    /**
     * Reads data from server socket to PDUs and sends through PETEP.
     */
    private void doReadFromServer() {
        var charset = ((UdpProxy) proxy).getConfig().getCharset();
        var bufferSize = ((UdpProxy) proxy).getConfig().getBufferSize();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                var buffer = new byte[bufferSize];
                var packet = new DatagramPacket(buffer, buffer.length);
                socketServer.receive(packet);
                var pdu = new UdpPdu(proxy, this, PduDestination.CLIENT, buffer, packet.getLength(), charset);
                process(pdu);
            }
        } catch (SocketException e) {
            // Socket closed
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "UDP connection exception occurred!", e);
        }
    }

    /**
     * Send data from specified queue to specified address and port using socket.
     */
    private void doSend(PduQueue queue, DatagramSocket socket, InetAddress address, int port) {
        // PDU
        PDU pdu;

        try {
            // Read bytes to buffer and send it to out stream.
            while ((pdu = queue.take()) != null) {
                var packet = new DatagramPacket(pdu.getBuffer(), pdu.getSize(), address, port);
                socket.send(packet);
            }
        } catch (SocketException e) {
            // Socket closed
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "UDP connection exception occurred!", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Cleans up connection.
     */
    private void cleanup() {
        stop();
    }
}
