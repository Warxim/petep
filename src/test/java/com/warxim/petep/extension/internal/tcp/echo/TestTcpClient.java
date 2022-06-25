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
package com.warxim.petep.extension.internal.tcp.echo;

import com.warxim.petep.test.proxy.common.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;

import static com.warxim.petep.test.proxy.common.Constant.*;

@Log
@RequiredArgsConstructor
public class TestTcpClient {
    private Socket socket;
    private final String targetIp;
    private final int targetPort;

    public void start() throws IOException {
        socket = new Socket(InetAddress.getByName(targetIp), targetPort);
    }

    public void send(Message message) {
        try {
            socket.getOutputStream().write(message.getData(), 0, message.getSize());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception during send!", e);
        }
    }

    public Message receive() {
        try {
            var buffer = new byte[BUFFER_SIZE];

            var size = socket.getInputStream().read(buffer);

            return new Message(buffer, size);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception during receive!", e);
        }
        return null;
    }

    public void close() throws IOException {
        socket.close();
    }
}
