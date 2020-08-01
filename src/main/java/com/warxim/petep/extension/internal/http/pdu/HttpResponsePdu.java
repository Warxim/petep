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

import java.util.Set;
import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.proxy.worker.Proxy;

public final class HttpResponsePdu extends HttpPdu {
  private int statusCode;
  private String statusMessage;

  public HttpResponsePdu(
      Proxy proxy,
      Connection connection,
      PduDestination destination,
      byte[] buffer,
      int size) {
    super(proxy, connection, destination, buffer, size);
  }

  public HttpResponsePdu(
      Proxy proxy,
      Connection connection,
      PduDestination destination,
      byte[] buffer,
      int size,
      Set<String> tags) {
    super(proxy, connection, destination, buffer, size, tags);
  }

  public String getStatusMessage() {
    return statusMessage;
  }

  public void setStatusMessage(String statusMessage) {
    this.statusMessage = statusMessage;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  @Override
  public PDU copy() {
    HttpResponsePdu pdu = new HttpResponsePdu(proxy, connection, destination, buffer.clone(), size);

    pdu.addTags(tags);

    pdu.setStatusCode(statusCode);
    pdu.setStatusMessage(statusMessage);
    pdu.setVersion(version);
    pdu.addHeaders(headers);

    return pdu;
  }
}
