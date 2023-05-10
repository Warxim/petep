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
package com.warxim.petep.extension.internal.tcp.proxy.util;

import com.warxim.petep.extension.internal.tcp.SslCertificateConfig;
import com.warxim.petep.extension.internal.tcp.SslConfig;
import com.warxim.petep.extension.internal.tcp.proxy.base.TcpProxyException;
import com.warxim.petep.util.FileUtils;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * Utilities for working with TCP socket factories
 */
public final class TcpSocketFactoryUtils {
    /**
     * TrustManager array with single trust-all manager
     */
    private static final TrustManager[] TRUST_ALL_MANAGERS = createTrustManagers();

    static {
        Security.setProperty("jdk.tls.disabledAlgorithms", "");
    }

    /**
     * Creates SSL socket factory with given config
     * @param sslConfig SSL/TLS configuration
     * @return SSL socket factory
     * @throws TcpProxyException if socket factory cannot be created
     */
    public static SSLSocketFactory createSslSocketFactory(SslConfig sslConfig) throws TcpProxyException {
        return createSslContext(sslConfig).getSocketFactory();
    }

    /**
     * Creates SSL server socket factory with given config
     * @param sslConfig SSL/TLS configuration
     * @return SSL socket factory
     * @throws TcpProxyException if socket factory cannot be created
     */
    public static SSLServerSocketFactory createSslServerSocketFactory(SslConfig sslConfig) throws TcpProxyException {
        return createSslContext(sslConfig).getServerSocketFactory();
    }

    /**
     * Creates SSL socket factory with given config
     */
    private static SSLContext createSslContext(SslConfig sslConfig) throws TcpProxyException {
        try {
            var sslContext = SSLContext.getInstance(sslConfig.getAlgorithm());
            sslContext.init(
                    getKeyManagers(sslConfig.getCertificateConfig()),
                    TRUST_ALL_MANAGERS,
                    new SecureRandom());
            return sslContext;
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

    private TcpSocketFactoryUtils() {

    }
}
