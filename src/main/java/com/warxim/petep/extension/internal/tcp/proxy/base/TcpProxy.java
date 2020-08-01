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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.warxim.petep.core.connection.ConnectionManager;
import com.warxim.petep.extension.internal.tcp.TcpConfig;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.proxy.module.ProxyModule;
import com.warxim.petep.proxy.worker.Proxy;

public abstract class TcpProxy extends Proxy {
  /** TCP connection manager. */
  protected final TcpConnectionManager connectionManager;

  /** TCP configuration. */
  protected final TcpConfig config;

  /** Socket factory for SSL/noSSL sockets. */
  protected TcpSocketFactory socketFactory;

  /** Server socket (between client and proxy). */
  protected ServerSocket socket = null;

  /** TCP proxy thread. */
  protected Thread thread;

  /** Is TCP proxy running? */
  protected boolean running;

  /** TCP proxy constructor. */
  public TcpProxy(ProxyModule module, PetepHelper helper, TcpConfig config) {
    super(module, helper);
    this.config = config;
    connectionManager = createConnectionManager();
  }

  /** Creates connection manager. */
  protected TcpConnectionManager createConnectionManager() {
    return new TcpConnectionManager();
  }

  /** Creates connection. */
  protected abstract TcpConnection createConnection(Socket socket);

  /** Prepares TCP socket factory using SSL configuration. */
  @Override
  public boolean prepare() {
    try {
      socketFactory =
          new TcpSocketFactory(config.getServerSslConfig(), config.getClientSslConfig());
    } catch (KeyManagementException | NoSuchAlgorithmException | UnrecoverableKeyException
        | KeyStoreException | CertificateException | IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "SSL exception.", e);
      return false;
    }

    return true;
  }

  /** Starts TCP proxy. */
  @Override
  public boolean start() {
    try {
      socket = socketFactory.createServerSocket(config.getProxyIP(), config.getProxyPort());

      thread = new Thread(this::accept);

      running = true;

      thread.start();

      return true;
    } catch (UnknownHostException e) {
      Logger.getGlobal().log(Level.SEVERE, "TCP unknown host exception thrown.", e);
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "TCP IO exception thrown.", e);
    }

    return false;
  }

  /** Accepts incoming connections. */
  protected void accept() {
    try {
      while (running) {
        // Accept connection.
        TcpConnection connection = createConnection(socket.accept());

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

  /** Stops TCP proxy. */
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

  /** Returns connection manager. */
  @Override
  public ConnectionManager getConnectionManager() {
    return connectionManager;
  }

  /** Returns TCP config. */
  public TcpConfig getConfig() {
    return config;
  }

  /** Returns TCP socket factory. */
  public TcpSocketFactory getSocketFactory() {
    return socketFactory;
  }
}
