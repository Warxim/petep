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

import com.warxim.petep.core.connection.ConnectionManager;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.udp.UdpConfig;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.proxy.module.ProxyModule;
import com.warxim.petep.proxy.worker.Proxy;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UDP proxy
 */
public class UdpProxy extends Proxy {
    private UdpConnectionManager connectionManager;
    private UdpConfig config;
    private Thread thread;
    /**
     * Socket between client and proxy.
     */
    private DatagramSocket socketClient;

    /**
     * Constructs UDP proxy
     * @param module Parent module of the worker
     * @param helper Helper for accessing running instance of PETEP core
     * @param config UDP configuration
     */
    public UdpProxy(ProxyModule module, PetepHelper helper, UdpConfig config) {
        super(module, helper);
        this.config = config;
        connectionManager = new UdpConnectionManager(helper);
    }

    @Override
    public boolean prepare() {
        return true;
    }

    @Override
    public boolean start() {
        try {
            socketClient = new DatagramSocket(config.getProxyPort(), InetAddress.getByName(config.getProxyIP()));
            thread = new Thread(this::doRead);
            thread.start();
            return true;
        } catch (UnknownHostException e) {
            Logger.getGlobal().log(Level.SEVERE, "UDP unknown host exception thrown.", e);
        } catch (SocketException e) {
            Logger.getGlobal().log(Level.SEVERE, "UDP socket exception thrown.", e);
        }
        return false;
    }

    @Override
    public void stop() {
        if (thread != null) {
            thread.interrupt();
        }

        if (socketClient != null) {
            socketClient.close();
        }

        connectionManager.stop();
    }

    @Override
    public boolean supports(PDU pdu) {
        return pdu instanceof UdpPdu;
    }

    @Override
    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public UdpConfig getConfig() {
        return config;
    }

    /**
     * Reads data from client and creates pseudo UDP connections.
     */
    protected void doRead() {
        var charset = config.getCharset();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                var buffer = new byte[config.getBufferSize()];
                var packet = new DatagramPacket(buffer, buffer.length);
                socketClient.receive(packet);

                var clientAddress = packet.getAddress();
                var clientPort = packet.getPort();
                var connectionCode = connectionManager.generateCode(clientAddress, clientPort);

                var connection = connectionManager.get(connectionCode).orElse(null);
                if (connection == null) {
                    connection = new UdpConnection(connectionCode, this, clientAddress, clientPort, socketClient);
                    if (!connection.start()) {
                        continue;
                    }
                    connectionManager.add(connection);
                }

                var pdu = new UdpPdu(this, connection, PduDestination.SERVER, buffer, packet.getLength(), charset);
                helper.processPdu(pdu);
            }
        } catch (SocketException e) {
            // Socket closed
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "UDP exception occurred!", e);
        }
    }
}
