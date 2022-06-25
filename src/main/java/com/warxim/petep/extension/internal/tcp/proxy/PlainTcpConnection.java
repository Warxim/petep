/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2020 Michal Válka
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
package com.warxim.petep.extension.internal.tcp.proxy;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.core.pdu.PduQueue;
import com.warxim.petep.extension.internal.tcp.proxy.base.TcpConnection;
import com.warxim.petep.extension.internal.tcp.proxy.base.TcpPdu;
import com.warxim.petep.proxy.worker.Proxy;

import java.io.IOException;
import java.net.Socket;

/**
 * Plain TCP connection.
 * <p>Simple implementation for basic TCP support.</p>
 */
public final class PlainTcpConnection extends TcpConnection {
    /**
     * TCP connection constructor.
     * @param code Unique code of the connection
     * @param proxy Proxy to which the connection belongs
     * @param socket Client socket for the connection
     */
    public PlainTcpConnection(String code, Proxy proxy, Socket socket) {
        super(code, proxy, socket);
    }

    @Override
    protected void readFromServer() {
        doRead(PduDestination.CLIENT, socketServer);
    }

    @Override
    protected void readFromClient() {
        doRead(PduDestination.SERVER, socketClient);
    }

    @Override
    protected void writeToServer() {
        doWrite(queueC2S, socketServer);
    }

    @Override
    protected void writeToClient() {
        doWrite(queueS2C, socketClient);
    }

    /**
     * Reads data from socket and process it in PETEP.
     */
    private void doRead(PduDestination destination, Socket socket) {
        // Size of data
        int size;
        int bufferSize = getConfig().getBufferSize();
        var charset = getConfig().getCharset();

        // Buffer
        byte[] buffer = new byte[bufferSize];

        try (var in = socket.getInputStream()) {
            // Read bytes to buffer and process it in PETEP.
            while ((size = in.read(buffer)) != -1) {
                // Create PDU from buffer
                var pdu = new TcpPdu(proxy, this, destination, buffer, size, charset);

                // Process PDU in PETEP.
                process(pdu);

                // Create new buffer.
                buffer = new byte[bufferSize];
            }
        } catch (IOException e) {
            // Closed
        }
    }

    /**
     * Writes data to socket.
     */
    private void doWrite(PduQueue queue, Socket socket) {
        // PDU
        PDU pdu;

        try (var out = socket.getOutputStream()) {
            // Read bytes to buffer and send it to out stream.
            while ((pdu = queue.take()) != null) {
                out.write(pdu.getBuffer(), 0, pdu.getSize());
            }
        } catch (IOException e) {
            // Closed socket
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
