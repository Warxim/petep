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

import com.warxim.petep.core.connection.ConnectionManager;
import com.warxim.petep.extension.internal.tcp.TcpConfig;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.proxy.module.ProxyModule;
import com.warxim.petep.proxy.worker.Proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base TCP proxy class for accepting TCP connections.
 */
public abstract class TcpProxy extends Proxy {
    /**
     * TCP connection manager.
     */
    protected final TcpConnectionManager connectionManager;

    /**
     * TCP configuration.
     */
    protected final TcpConfig config;

    /**
     * Socket factory for SSL/noSSL sockets.
     */
    protected TcpSocketFactory socketFactory;

    /**
     * Socket for accepting connection between Client &lt;-&gt; Proxy
     */
    protected ServerSocket socket;

    /**
     * TCP proxy thread.
     */
    protected Thread thread;

    /**
     * Is TCP proxy running?
     */
    protected boolean running;

    /**
     * Constructs TCP proxy
     * @param module Parent module of the worker
     * @param helper Helper for accessing running instance of PETEP core
     * @param config TCP configuration
     */
    protected TcpProxy(ProxyModule module, PetepHelper helper, TcpConfig config) {
        super(module, helper);
        this.config = config;
        connectionManager = createConnectionManager();
    }

    /**
     * Creates TCP connection.
     * @param socket Socket of the connection
     * @return TCP connection
     */
    protected abstract TcpConnection createConnection(Socket socket);

    /**
     * Creates connection manager.
     * @return TCP connection manager
     */
    protected TcpConnectionManager createConnectionManager() {
        return new TcpConnectionManager(helper);
    }

    /**
     * Prepares TCP socket factory using SSL configuration.
     */
    @Override
    public boolean prepare() {
        try {
            socketFactory = createTcpSocketFactory();
        } catch (TcpProxyException e) {
            Logger.getGlobal().log(Level.SEVERE, "SSL exception.", e);
            return false;
        }

        return true;
    }

    /**
     * Starts TCP proxy.
     * <p>Runs accept loop for accepting new connections, adding them to connection manager and starting them.</p>
     */
    @Override
    public boolean start() {
        try {
            socket = socketFactory.createC2PSocket(config.getProxyIP(), config.getProxyPort());

            thread = new Thread(this::accept);

            running = true;

            thread.start();

            return true;
        } catch (UnknownHostException e) {
            Logger.getGlobal().log(Level.SEVERE, "TCP unknown host exception thrown.", e);
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "TCP IO exception thrown.", e);
        } catch (IllegalArgumentException e) {
            Logger.getGlobal().log(Level.SEVERE, "Invalid arguments provided.", e);
        }

        return false;
    }

    /**
     * Creates TCP socket factory from current TCP config
     */
    protected TcpSocketFactory createTcpSocketFactory() throws TcpProxyException {
        return new TcpSocketFactory(config.getServerSslConfig(), config.getClientSslConfig());
    }

    /**
     * Accepts incoming connections.
     * <p>Adds accepted connections to connection manager.</p>
     */
    protected void accept() {
        try {
            while (running) {
                // Accept connection.
                var connection = createConnection(socket.accept());

                // Start connection and add it to connection manager.
                if (connection.start()) {
                    connectionManager.add(connection);
                }
            }
        } catch (SocketException e) {
            // Socket closed
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "TCP Proxy accept exception.", e);
        }
    }

    /**
     * Stops TCP proxy.
     */
    @Override
    public void stop() {
        running = false;

        // Interrupt accepting thread.
        if (thread != null) {
            thread.interrupt();
        }

        // Close server socket.
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                Logger.getGlobal().log(Level.SEVERE, "TCP proxy exception - IO exception!", e);
            }
        }

        // Stop connections.
        connectionManager.stop();
    }

    @Override
    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    /**
     * Gets TCP config.
     * @return TCP config of this proxy
     */
    public TcpConfig getConfig() {
        return config;
    }

    /**
     * Gets TCP socket factory.
     * @return TCP socket factory for this proxy (created according to the config)
     */
    public TcpSocketFactory getSocketFactory() {
        return socketFactory;
    }

}
