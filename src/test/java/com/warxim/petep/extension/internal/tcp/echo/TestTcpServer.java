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

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static com.warxim.petep.test.proxy.common.Constant.BUFFER_SIZE;

@Log
@RequiredArgsConstructor
public class TestTcpServer extends Thread {
    private final String ip;
    private final int port;
    private final int parallelConnections;

    public void run() {
        var executor = Executors.newFixedThreadPool(parallelConnections);
        try (var serverSocket = new ServerSocket(port, 0, InetAddress.getByName(ip))) {
            while (true) {
                var clientSocket = serverSocket.accept();
                executor.submit(() -> handleConnection(clientSocket));
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Exception in server!", e);
        }

        // Shutdown
        executor.shutdown();
        try {
            if (executor.awaitTermination(1, TimeUnit.MINUTES)) {
                return;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        executor.shutdownNow();
    }

    private void handleConnection(Socket clientSocket) {
        int size;
        var buffer = new byte[BUFFER_SIZE];
        try (clientSocket) {
            var in = clientSocket.getInputStream();
            var out = clientSocket.getOutputStream();
            while ((size = in.read(buffer)) != -1) {
                out.write(buffer, 0, size);
            }
        } catch (IOException e) {
            // Closed
        }
    }
}
