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
package com.warxim.petep.extension.internal.external_http_proxy;

/** External HTTP proxy configuration. */
public final class EHTTPPConfig {
  private String serverIp;
  private int serverPort;

  private String proxyIp;
  private int proxyPort;

  /** External HTTP configuration constructor. */
  public EHTTPPConfig(String serverIp, int serverPort, String proxyIp, int proxyPort) {
    this.serverIp = serverIp;
    this.serverPort = serverPort;
    this.proxyIp = proxyIp;
    this.proxyPort = proxyPort;
  }

  public String getServerIp() {
    return serverIp;
  }

  public void setServerIp(String ip) {
    serverIp = ip;
  }

  public int getServerPort() {
    return serverPort;
  }

  public void setServerPort(int port) {
    serverPort = port;
  }

  public String getProxyIp() {
    return proxyIp;
  }

  public void setProxyIp(String ip) {
    proxyIp = ip;
  }

  public int getProxyPort() {
    return proxyPort;
  }

  public void setProxyPort(int port) {
    proxyPort = port;
  }

  public void set(String serverIp, int serverPort, String proxyIp, int proxyPort) {
    this.serverIp = serverIp;
    this.serverPort = serverPort;
    this.proxyIp = proxyIp;
    this.proxyPort = proxyPort;
  }
}
