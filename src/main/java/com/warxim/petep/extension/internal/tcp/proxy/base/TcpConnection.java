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
package com.warxim.petep.extension.internal.tcp.proxy.base;

import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.extension.internal.tcp.TcpConfig;
import com.warxim.petep.proxy.worker.Proxy;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TCP connection base class.
 * <p>Uses 4 threads (one for each stream) for processing input/output:</p>
 * <ul>
 *     <li>Read from server</li>
 *     <li>Read from client</li>
 *     <li>Write to server</li>
 *     <li>Write to client</li>
 * </ul>
 */
public abstract class TcpConnection extends Connection {
    /**
     * Socket between client and proxy.
     */
    protected final Socket socketClient;

    /**
     * Writers and readers executor.
     */
    protected ExecutorService executor;

    /**
     * Socket between proxy and server.
     */
    protected Socket socketServer;

    /**
     * Is connection in closing state?
     */
    protected AtomicBoolean closing = new AtomicBoolean(false);

    /**
     * TCP connection constructor.
     * @param code Unique code of the connection
     * @param proxy Proxy to which the connection belongs
     * @param socket Client socket for the connection
     */
    protected TcpConnection(String code, Proxy proxy, Socket socket) {
        super(code, proxy);
        socketClient = socket;
    }

    /**
     * Reads data from server to PDUs and sends them through the PETEP to the client.
     */
    protected abstract void readFromServer();

    /**
     * Reads data from client to PDUs and sends them through the PETEP to the server.
     */
    protected abstract void readFromClient();

    /**
     * Gets data from the PETEP (from queueC2S) and sends them to the server.
     */
    protected abstract void writeToServer();

    /**
     * Gets data from the PETEP (from queueS2C) and sends them to the client.
     */
    protected abstract void writeToClient();

    /**
     * Starts connection.
     */
    @Override
    public boolean start() {
        Logger.getGlobal().info(() -> String.format("Starting TCP connection with code '%s'...", code));

        executor = Executors.newFixedThreadPool(4);

        // Open connection with server
        try {
            var tcpProxy = (TcpProxy) proxy;
            socketServer = tcpProxy.getSocketFactory()
                    .createSocket(tcpProxy.getConfig().getTargetIP(), tcpProxy.getConfig().getTargetPort());

            // Write threads
            executor.execute(this::writeToServer);
            executor.execute(this::writeToClient);

            // Read threads
            executor.execute(this::readFromClient);
            executor.execute(this::readFromServer);

            // Automatic cleanup
            executor.execute(this::cleanup);

            Logger.getGlobal().info(() -> String.format("TCP connection with code '%s' started.", code));

            return true;
        } catch (UnknownHostException e) {
            Logger.getGlobal().log(Level.SEVERE, "TCP connection exception - unknown host!", e);
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "TCP connection exception - IO exception!", e);
        }

        executor.shutdownNow();

        return false;
    }

    /**
     * Stops connection.
     */
    @Override
    public void stop() {
        // Do not run stop if the connection is already closing.
        if (!closing.compareAndSet(false, true)) {
            return;
        }

        Logger.getGlobal().info(() -> String.format("Stopping TCP connection with code '%s'...", code));

        // Shutdown threads.
        executor.shutdownNow();

        // Close socket to client.
        try {
            if (socketClient != null) {
                socketClient.close();
            }
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "TCP connection exception - IO exception!", e);
        }

        // Close socket to server.
        try {
            if (socketServer != null) {
                socketServer.close();
            }
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "TCP connection exception - IO exception!", e);
        }

        // Remove connection from connection manager.
        proxy.getConnectionManager().remove(this);

        Logger.getGlobal().info(() -> String.format("TCP connection with code '%s' stopped.", code));
    }

    /**
     * Cleans up connection.
     */
    private void cleanup() {
        // Wait for remaining data processing before the connection is permanently closed.
        try {
            Thread.sleep(((TcpProxy) proxy).getConfig().getConnectionCloseDelay());
        } catch (InterruptedException e) {
            // Interrupted
            Thread.currentThread().interrupt();
        }

        stop();
    }

    /**
     * About connection.
     */
    @Override
    public String toString() {
        return "TCP connection "
                + code
                + " ("
                + socketClient.getInetAddress().getHostAddress()
                + ":"
                + socketClient.getPort()
                + ")";
    }

    /**
     * Gets TCP config from proxy.
     * @return TCP config
     */
    protected TcpConfig getConfig() {
        return ((TcpProxy) proxy).getConfig();
    }
}
