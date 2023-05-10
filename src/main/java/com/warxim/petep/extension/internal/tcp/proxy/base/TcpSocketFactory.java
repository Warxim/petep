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

import com.warxim.petep.extension.internal.tcp.SslConfig;
import com.warxim.petep.extension.internal.tcp.proxy.util.TcpSocketFactoryUtils;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Security;

/**
 * TCP socket factory.
 * <p>Creates TCP sockets according to the configuration.</p>
 */
public final class TcpSocketFactory {
    /**
     * Socket factory between the client and the proxy (server socket factory)
     */
    private final ServerSocketFactory c2pSocketFactory;

    /**
     * Socket factory between the proxy and the server (client socket factory)
     */
    private final SocketFactory p2sSocketFactory;

    /**
     * TCP socket factory constructor.
     * @param c2pConfig SSL configuration for server
     * @param p2sConfig SSL configuration for client
     * @throws TcpProxyException If the socket factory could not be initialized
     */
    public TcpSocketFactory(SslConfig c2pConfig, SslConfig p2sConfig) throws TcpProxyException {
        if (c2pConfig != null || p2sConfig != null) {
            // Unset disabled algorithms.
            Security.setProperty("jdk.tls.disabledAlgorithms", "");
        }

        // Initialize server socket factory.
        if (c2pConfig != null) {
            c2pSocketFactory = TcpSocketFactoryUtils.createSslServerSocketFactory(c2pConfig);
        } else {
            c2pSocketFactory = ServerSocketFactory.getDefault();
        }

        // Initialize client socket factory.
        if (p2sConfig != null) {
            p2sSocketFactory = TcpSocketFactoryUtils.createSslSocketFactory(p2sConfig);
        } else {
            p2sSocketFactory = SocketFactory.getDefault();
        }
    }

    /**
     * Creates server socket for specified host and port for Client &lt;-&gt; Proxy communication.
     * @param host Host on which to bind the socket
     * @param port Port on which to bind the socket
     * @return Created server socket for Client &lt;-&gt; Proxy communication
     * @throws IOException If the socket could not be created
     */
    public ServerSocket createC2PSocket(String host, int port) throws IOException {
        return c2pSocketFactory.createServerSocket(port, 0, InetAddress.getByName(host));
    }

    /**
     * Creates socket for specified host and port for Proxy &lt;-&gt; Server communication.
     * @param host Host on which to bind the socket
     * @param port Port on which to bind the socket
     * @return Created client socket for Proxy &lt;-&gt; Server communication
     * @throws IOException If the socket could not be created
     */
    public Socket createP2SSocket(String host, int port) throws IOException {
        return p2sSocketFactory.createSocket(host, port);
    }
}
