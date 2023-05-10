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
package com.warxim.petep.extension.internal.tcp.proxy.starttls;

import com.warxim.petep.extension.internal.tcp.SslConfig;
import com.warxim.petep.extension.internal.tcp.proxy.base.TcpProxyException;
import com.warxim.petep.extension.internal.tcp.proxy.util.TcpSocketFactoryUtils;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Component for wrapping sockets into SSL sockets
 */
public class StarttlsSocketWrapper {
    /**
     * Client-Proxy socket factory
     */
    private final SSLSocketFactory c2pSocketFactory;

    /**
     * Proxy-Server socket factory
     */
    private final SSLSocketFactory p2sSocketFactory;

    /**
     * Creates STARTTLS socket wrapper component
     * @param c2pConfig Config of SSL between the client and the proxy
     * @param p2sConfig Config of SSL between the proxy and the server
     */
    public StarttlsSocketWrapper(SslConfig c2pConfig, SslConfig p2sConfig) throws TcpProxyException {
            if (c2pConfig == null || p2sConfig == null) {
                throw new TcpProxyException("Client and server SSL configs have to exists for STARTTLS!");
            }

            c2pSocketFactory = TcpSocketFactoryUtils.createSslSocketFactory(c2pConfig);
            p2sSocketFactory = TcpSocketFactoryUtils.createSslSocketFactory(p2sConfig);
    }

    /**
     * Wrap socket for client to proxy communication in SSL/TLS
     * @param socket Socket to wrap
     * @param consumedBytes Bytes that were already consumed and that are part of the handshake
     * @return Wrapped socket (server mode)
     */
    public SSLSocket wrapC2PSocket(Socket socket, byte[] consumedBytes) throws IOException {
        var sslSocket = (SSLSocket) c2pSocketFactory.createSocket(socket, new ByteArrayInputStream(consumedBytes), true);
        sslSocket.setUseClientMode(false);
        sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
        return sslSocket;
    }

    /**
     * Wrap socket for proxy to server communication in SSL/TLS
     * @param socket Socket to wrap
     * @return Wrapped socket (client mode)
     */
    public Socket wrapP2SSocket(Socket socket) throws IOException {
        var sslSocket = (SSLSocket) p2sSocketFactory.createSocket(socket, null, true);
        sslSocket.setUseClientMode(true);
        sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
        return sslSocket;
    }
}
