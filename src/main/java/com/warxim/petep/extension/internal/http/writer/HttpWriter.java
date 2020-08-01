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

public abstract class HttpWriter extends PduWriter {
  public HttpWriter(OutputStream out) {
    super(out);
  }

  public abstract void write(PDU pdu) throws IOException;

  protected final void writeHeaders(HttpPdu pdu) throws IOException {
    if (pdu.getHeaders().isEmpty()) {
      return;
    }

    if (pdu.getHeader("Content-Length") != null) {
      pdu.addHeader("Content-Length", String.valueOf(pdu.getSize()));
    }

    for (var header : pdu.getHeaders().entrySet()) {
      out.write(header.getKey().getBytes());
      out.write(": ".getBytes());
      out.write(header.getValue().getBytes());
      out.write("\r\n".getBytes());
    }

    out.write("\r\n".getBytes());
  }

  protected final void writeBody(HttpPdu pdu) throws IOException {
    if (pdu.getSize() == 0) {
      return;
    }

    if (pdu.hasTag("chunk") || pdu.hasTag("last_chunk")) {
      out.write(Integer.toHexString(pdu.getSize()).getBytes());
      out.write("\r\n".getBytes());
      out.write(pdu.getBuffer(), 0, pdu.getSize());
      out.write("\r\n".getBytes());

      if (pdu.hasTag("last_chunk")) {
        out.write("0\r\n\r\n".getBytes());
      }
    } else {
      out.write(pdu.getBuffer(), 0, pdu.getSize());
    }
  }
}
