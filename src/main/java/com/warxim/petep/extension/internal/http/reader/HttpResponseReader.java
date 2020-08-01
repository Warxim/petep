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
package com.warxim.petep.extension.internal.http.reader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.http.pdu.HttpResponsePdu;

public final class HttpResponseReader extends HttpReader {
  protected enum StatusLineState {
    VERSION, STATUS_CODE, STATUS_MESSAGE
  }

  public HttpResponseReader(InputStream in, int maxLength, Charset defaultCharset) {
    super(in, maxLength, defaultCharset);
  }

  @Override
  public HttpResponsePdu read() throws IOException {
    HttpResponsePdu response = new HttpResponsePdu(null, null, PduDestination.CLIENT, null, 0);

    if (internalState == null) {
      if (!readStatusLine(response) || !readHeaders(response)) {
        return null;
      }

      processHeaders(response);

      readBody(response);
    } else {
      readBody(response);
    }

    return response;
  }

  private boolean readStatusLine(HttpResponsePdu response) throws IOException {
    StatusLineState state = StatusLineState.VERSION;

    StringBuilder builder = new StringBuilder();

    int currentByte;
    while ((currentByte = in.read()) != -1) {
      switch (state) {
        case VERSION:
          if (currentByte == ' ') {
            response.setVersion(builder.toString());
            builder.setLength(0);
            state = StatusLineState.STATUS_CODE;
            break;
          }

          builder.append((char) currentByte);
          break;
        case STATUS_CODE:
          if (currentByte == ' ') {
            response.setStatusCode(Integer.parseInt(builder.toString()));
            builder.setLength(0);
            state = StatusLineState.STATUS_MESSAGE;
            break;
          }

          builder.append((char) currentByte);
          break;
        case STATUS_MESSAGE:
          if (currentByte == '\r') {
            // Skip \n.
            skip(1);

            response.setStatusMessage(builder.toString());
            return true;
          }

          builder.append((char) currentByte);
          break;
      }
    }
    return false;
  }
}
