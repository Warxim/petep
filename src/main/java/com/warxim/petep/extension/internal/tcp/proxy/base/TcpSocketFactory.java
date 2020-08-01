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

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import com.warxim.petep.extension.internal.tcp.SslCertificateConfig;
import com.warxim.petep.extension.internal.tcp.SslConfig;
import com.warxim.petep.util.FileUtils;

/** TCP socket factory. */
public final class TcpSocketFactory {
  // Factories
  private final ServerSocketFactory serverSocketFactory;
  private final SocketFactory clientSocketFactory;

  /** TCP socket factory constructor. */
  public TcpSocketFactory(SslConfig server, SslConfig client)
      throws NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException,
      KeyStoreException, IOException, CertificateException {
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
      SSLContext serverSslContext = SSLContext.getInstance(server.getAlgorithm());
      serverSslContext.init(getKeyManagers(server.getCertificateConfig()), truster,
          new java.security.SecureRandom());
      serverSocketFactory = serverSslContext.getServerSocketFactory();
    } else {
      serverSocketFactory = ServerSocketFactory.getDefault();
    }

    // Initialize client socket factory.
    if (client != null) {
      SSLContext clientSslContext = SSLContext.getInstance(client.getAlgorithm());
      clientSslContext.init(getKeyManagers(client.getCertificateConfig()), truster,
          new java.security.SecureRandom());
      clientSocketFactory = clientSslContext.getSocketFactory();
    } else {
      clientSocketFactory = SocketFactory.getDefault();
    }
  }

  /** Creates key managers. */
  private static final KeyManager[] getKeyManagers(SslCertificateConfig config)
      throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException,
      UnrecoverableKeyException {
    if (config == null) {
      return null;
    }

    // Create key manager factory.
    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");

    // Initialize key stores.
    try (FileInputStream in =
        new FileInputStream(FileUtils.getProjectFileAbsolutePath(config.getKeyStore()))) {
      KeyStore keyStore = KeyStore.getInstance(config.getKeyStoreType());

      keyStore.load(in, config.getKeyStorePassword().toCharArray());

      keyManagerFactory.init(keyStore, config.getKeyPassword().toCharArray());
    }

    return keyManagerFactory.getKeyManagers();
  }

  /** Creates trust manager that trusts everything. */
  private static final TrustManager[] createTrustManagers() {
    return new TrustManager[] {new X509ExtendedTrustManager() {
      @Override
      public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
        // Trust everyone.
      }

      @Override
      public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
        // Trust everyone.
      }

      @Override
      public X509Certificate[] getAcceptedIssuers() {
        return null;
      }

      @Override
      public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) {
        // Trust everyone.
      }

      @Override
      public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) {
        // Trust everyone.
      }

      @Override
      public void checkClientTrusted(
          X509Certificate[] x509Certificates,
          String s,
          SSLEngine sslEngine) {
        // Trust everyone.
      }

      @Override
      public void checkServerTrusted(
          X509Certificate[] x509Certificates,
          String s,
          SSLEngine sslEngine) {
        // Trust everyone.
      }
    }};
  }

  /** Creates server socket for specified ip and port. */
  public ServerSocket createServerSocket(String ip, int port) throws IOException {
    return serverSocketFactory.createServerSocket(port, 0, InetAddress.getByName(ip));
  }

  /** Creates socket for specified ip and port. */
  public Socket createSocket(String ip, int port) throws IOException {
    return clientSocketFactory.createSocket(ip, port);
  }
}
