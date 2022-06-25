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

import com.warxim.petep.extension.internal.tcp.SslCertificateConfig;
import com.warxim.petep.extension.internal.tcp.SslConfig;
import com.warxim.petep.util.FileUtils;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * TCP socket factory.
 * <p>Creates TCP sockets according to the configuration.</p>
 */
public final class TcpSocketFactory {
    // Factories
    private final ServerSocketFactory serverSocketFactory;
    private final SocketFactory clientSocketFactory;

    /**
     * TCP socket factory constructor.
     * @param server SSL configuration for server
     * @param client SSL configuration for client
     * @throws TcpProxyException If the socket factory could not be initialized
     */
    public TcpSocketFactory(SslConfig server, SslConfig client) throws TcpProxyException {
        try {
            TrustManager[] truster;

            if (server != null || client != null) {
                // Unset disabled algorithms.
                Security.setProperty("jdk.tls.disabledAlgorithms", "");

                truster = createTrustManagers();
            } else {
                truster = null;
            }

            // Initialize server socket factory.
            if (server != null) {
                var serverSslContext = SSLContext.getInstance(server.getAlgorithm());
                serverSslContext.init(
                        getKeyManagers(server.getCertificateConfig()),
                        truster,
                        new SecureRandom());
                serverSocketFactory = serverSslContext.getServerSocketFactory();
            } else {
                serverSocketFactory = ServerSocketFactory.getDefault();
            }

            // Initialize client socket factory.
            if (client != null) {
                var clientSslContext = SSLContext.getInstance(client.getAlgorithm());
                clientSslContext.init(
                        getKeyManagers(client.getCertificateConfig()),
                        truster,
                        new SecureRandom());
                clientSocketFactory = clientSslContext.getSocketFactory();
            } else {
                clientSocketFactory = SocketFactory.getDefault();
            }
        } catch (UnrecoverableKeyException
                | CertificateException
                | NoSuchAlgorithmException
                | IOException
                | KeyStoreException
                | KeyManagementException e) {
            throw new TcpProxyException("Could not initialize TCP socket factory!", e);
        }
    }

    /**
     * Creates key managers.
     */
    private static KeyManager[] getKeyManagers(SslCertificateConfig config)
            throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException, UnrecoverableKeyException {
        if (config == null) {
            return new KeyManager[0];
        }

        // Create key manager factory.
        var keyManagerFactory = KeyManagerFactory.getInstance("SunX509");

        // Initialize key stores.
        try (var in = new FileInputStream(FileUtils.getProjectFileAbsolutePath(config.getKeyStore()))) {
            var keyStore = KeyStore.getInstance(config.getKeyStoreType());

            keyStore.load(in, config.getKeyStorePassword().toCharArray());

            keyManagerFactory.init(keyStore, config.getKeyPassword().toCharArray());
        }

        return keyManagerFactory.getKeyManagers();
    }

    /**
     * Creates trust manager that trusts everything.
     */
    private static TrustManager[] createTrustManagers() {
        return new TrustManager[] {
                new TrustEveryoneTrustManager()
        };
    }

    /**
     * Creates server socket for specified host and port.
     * @param host Host on which to bind the socket
     * @param port Port on which to bind the socket
     * @return Created server socket
     * @throws IOException If the socket could not be created
     */
    public ServerSocket createServerSocket(String host, int port) throws IOException {
        return serverSocketFactory.createServerSocket(port, 0, InetAddress.getByName(host));
    }

    /**
     * Creates socket for specified host and port.
     * @param host Host on which to bind the socket
     * @param port Port on which to bind the socket
     * @return Created client socket
     * @throws IOException If the socket could not be created
     */
    public Socket createSocket(String host, int port) throws IOException {
        return clientSocketFactory.createSocket(host, port);
    }
}
