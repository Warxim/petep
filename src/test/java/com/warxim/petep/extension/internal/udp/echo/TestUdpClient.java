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
package com.warxim.petep.extension.internal.udp.echo;

import com.warxim.petep.test.proxy.common.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;

import static com.warxim.petep.test.proxy.common.Constant.*;

@Log
@RequiredArgsConstructor
public class TestUdpClient {
    private DatagramSocket socket;
    private final String targetIp;
    private final int targetPort;

    public void start() throws SocketException {
        socket = new DatagramSocket();
    }

    public void send(Message message) {
        try {
            // Send data
            var packet = new DatagramPacket(message.getData(), message.getSize(), InetAddress.getByName(targetIp), targetPort);
            socket.send(packet);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception during send!", e);
        }
    }

    public Message receive() {
        try {
            var buffer = new byte[BUFFER_SIZE];

            // Receive data
            var packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            return new Message(packet.getData(), packet.getLength());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception during receive!", e);
        }
        return null;
    }

    public void close() {
        socket.close();
    }
}