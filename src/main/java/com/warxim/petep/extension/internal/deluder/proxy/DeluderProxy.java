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

import com.warxim.petep.core.connection.ConnectionManager;
import com.warxim.petep.core.pdu.DefaultPdu;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.proxy.module.ProxyModule;
import com.warxim.petep.proxy.worker.Proxy;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Deluder Proxy
 * <p>Simple proxy implementation for integration between Deluder and PETEP.</p>
 * <p>Built on a simple client-server communication protocol.</p>
 */
public final class DeluderProxy extends Proxy {
    private final DeluderConnectionManager connectionManager;
    private final DeluderConfig config;
    private Thread thread;
    private ServerSocket socket;

    /**
     * Constructs Deluder Proxy
     * @param module Parent module of the worker
     * @param helper Helper for accessing running instance of PETEP core
     * @param config TCP configuration
     */
    public DeluderProxy(ProxyModule module, PetepHelper helper, DeluderConfig config) {
        super(module, helper);
        this.config = config;
        connectionManager = new DeluderConnectionManager(helper);
    }

    @Override
    public boolean prepare() {
        return true;
    }

    @Override
    public boolean start() {
        try {
            socket = ServerSocketFactory.getDefault().createServerSocket(
                    config.getPetepPort(),
                    0,
                    InetAddress.getByName(config.getPetepHost())
            );

            thread = new Thread(this::accept);

            thread.start();

            return true;
        } catch (UnknownHostException e) {
            Logger.getGlobal().log(Level.SEVERE, "Unknown host exception thrown.", e);
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "IO exception thrown.", e);
        }
        return false;
    }

    @Override
    public void stop() {
        // Interrupt accepting thread
        if (thread != null) {
            thread.interrupt();
        }

        // Close server socket
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                Logger.getGlobal().log(Level.SEVERE, "Could not close server socket in Deluder proxy!", e);
            }
        }

        // Stop connections
        connectionManager.stop();
    }

    @Override
    public boolean supports(PDU pdu) {
        return pdu instanceof DefaultPdu;
    }

    @Override
    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    /**
     * Gets Deluder config.
     * @return Deluder config of this proxy
     */
    public DeluderConfig getConfig() {
        return config;
    }

    private void accept() {
        try {
            while (true) {
                // Accept connection.
                var connection = new DeluderConnection(connectionManager.nextCode(), this, socket.accept());

                // Start connection and add it to connection manager.
                if (connection.start()) {
                    connectionManager.add(connection);
                }
            }
        } catch (SocketException e) {
            // Socket closed
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Deluder Proxy accept exception.", e);
        }
    }
}
