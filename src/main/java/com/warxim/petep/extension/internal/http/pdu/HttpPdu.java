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
package com.warxim.petep.extension.internal.http.pdu;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.tcp.proxy.base.TcpPdu;
import com.warxim.petep.proxy.worker.Proxy;

public abstract class HttpPdu extends TcpPdu {
  protected String version;
  protected Map<String, String> headers;

  public HttpPdu(
      Proxy proxy,
      Connection connection,
      PduDestination destination,
      byte[] buffer,
      int size) {
    super(proxy, connection, destination, buffer, size);
    headers = new HashMap<>();
  }

  public HttpPdu(
      Proxy proxy,
      Connection connection,
      PduDestination destination,
      byte[] buffer,
      int size,
      Set<String> tags) {
    super(proxy, connection, destination, buffer, size, tags);
    headers = new HashMap<>();
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public void addHeaders(Map<String, String> headers) {
    this.headers.putAll(headers);
  }

  public void setHeaders(Map<String, String> headers) {
    this.headers = headers;
  }

  public void addHeader(String name, String value) {
    headers.put(name, value);
  }

  public void removeHeader(String name) {
    headers.remove(name);
  }

  public String getHeader(String name) {
    return headers.get(name);
  }
}
