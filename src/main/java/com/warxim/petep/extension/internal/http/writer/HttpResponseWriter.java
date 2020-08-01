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
package com.warxim.petep.extension.internal.http.writer;

import java.io.IOException;
import java.io.OutputStream;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.internal.http.pdu.HttpPdu;
import com.warxim.petep.extension.internal.http.pdu.HttpResponsePdu;

public final class HttpResponseWriter extends HttpWriter {
  public HttpResponseWriter(OutputStream out) {
    super(out);
  }

  @Override
  public void write(PDU response) throws IOException {
    writeStatusLine((HttpResponsePdu) response);
    writeHeaders((HttpPdu) response);
    writeBody((HttpPdu) response);
  }

  private void writeStatusLine(HttpResponsePdu response) throws IOException {
    if (response.getVersion() == null) {
      return;
    }

    out.write(response.getVersion().getBytes());
    out.write(' ');
    out.write(String.valueOf(response.getStatusCode()).getBytes());
    out.write(' ');
    out.write(response.getStatusMessage().getBytes());
    out.write("\r\n".getBytes());
  }
}
