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
package com.warxim.petep.extension.internal.tcp;

/** TCP configuration. */
public final class TcpConfig {
  private final String proxyIP;
  private final String targetIP;

  private final int proxyPort;
  private final int targetPort;

  private final int bufferSize;
  private final String charset;

  private final int connectionCloseDelay;

  private final SslConfig serverSslConfig;
  private final SslConfig clientSslConfig;

  /** TCP configuration constructor. */
  public TcpConfig(
      String proxyIP,
      String targetIP,
      int proxyPort,
      int targetPort,
      int bufferSize,
      String charset,
      int connectionCloseDelay,
      SslConfig serverSslConfig,
      SslConfig clientSslConfig) {
    this.proxyIP = proxyIP;
    this.targetIP = targetIP;
    this.proxyPort = proxyPort;
    this.targetPort = targetPort;
    this.bufferSize = bufferSize;
    this.charset = charset;
    this.connectionCloseDelay = connectionCloseDelay;
    this.serverSslConfig = serverSslConfig;
    this.clientSslConfig = clientSslConfig;
  }

  public String getProxyIP() {
    return proxyIP;
  }

  public String getTargetIP() {
    return targetIP;
  }

  public int getProxyPort() {
    return proxyPort;
  }

  public int getTargetPort() {
    return targetPort;
  }

  public int getBufferSize() {
    return bufferSize;
  }

  public String getCharset() {
    return charset;
  }

  public int getConnectionCloseDelay() {
    return connectionCloseDelay;
  }

  public SslConfig getServerSslConfig() {
    return serverSslConfig;
  }

  public SslConfig getClientSslConfig() {
    return clientSslConfig;
  }
}
