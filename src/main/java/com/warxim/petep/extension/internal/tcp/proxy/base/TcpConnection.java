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
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.extension.internal.tcp.TcpConfig;
import com.warxim.petep.proxy.worker.Proxy;

public abstract class TcpConnection extends Connection {
  /** Writers and readers executor. */
  protected final ExecutorService executor;

  /** Socket between client and proxy. */
  protected final Socket socketClient;

  /** Socket between proxy and server. */
  protected Socket socketServer;

  /** Is connection in closing state? */
  protected boolean closing = false;

  /** TCP connection constructor. */
  public TcpConnection(int id, Proxy proxy, Socket socket) {
    super(id, proxy);
    socketClient = socket;
    executor = Executors.newFixedThreadPool(4);
  }

  /** Starts connection. */
  @Override
  public boolean start() {
    Logger.getGlobal().info("Starting TCP connection " + id + "...");

    // Open connection with server
    try {
      socketServer = ((TcpProxy) proxy).getSocketFactory()
          .createSocket(((TcpProxy) proxy).getConfig().getTargetIP(),
              ((TcpProxy) proxy).getConfig().getTargetPort());

      // Write threads
      executor.execute(this::writeToServer);
      executor.execute(this::writeToClient);

      // Read threads
      executor.execute(this::readFromClient);
      executor.execute(this::readFromServer);

      // Automatic cleanup
      executor.execute(this::cleanup);

      Logger.getGlobal().info("TCP connection " + id + " started.");

      return true;
    } catch (UnknownHostException e) {
      Logger.getGlobal().log(Level.SEVERE, "TCP connection exception - unknown host!", e);
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "TCP connection exception - IO exception!", e);
    }

    return false;
  }

  protected abstract void readFromServer();

  protected abstract void readFromClient();

  protected abstract void writeToServer();

  protected abstract void writeToClient();

  /** Stops connection. */
  @Override
  public void stop() {
    // Do not run stop if the connection is already closing.
    if (closing) {
      return;
    }

    Logger.getGlobal().info("Stopping TCP connection " + id + "...");

    // Enable closing state.
    closing = true;

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
    ((TcpConnectionManager) proxy.getConnectionManager()).remove(this);

    Logger.getGlobal().info("TCP connection " + id + " stopped.");
  }

  /** Cleans up connection. */
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

  /** About connection. */
  @Override
  public String toString() {
    return "TCP connection " + id + " (" + socketClient.getInetAddress().getHostAddress() + ":"
        + socketClient.getPort() + ")";
  }

  protected TcpConfig getConfig() {
    return ((TcpProxy) proxy).getConfig();
  }
}
