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
import com.warxim.petep.extension.internal.http.pdu.HttpRequestPdu;

public final class HttpRequestReader extends HttpReader {
  protected enum RequestLineState {
    METHOD, PATH, VERSION
  }

  public HttpRequestReader(InputStream in, int maxLength, Charset defaultCharset) {
    super(in, maxLength, defaultCharset);
  }

  @Override
  public HttpRequestPdu read() throws IOException {
    HttpRequestPdu request = new HttpRequestPdu(null, null, PduDestination.SERVER, null, 0);

    if (internalState == null) {
      if (!readRequestLine(request) || !readHeaders(request)) {
        return null;
      }

      processHeaders(request);

      readBody(request);
    } else {
      readBody(request);
    }

    return request;
  }

  private boolean readRequestLine(HttpRequestPdu request) throws IOException {
    RequestLineState state = RequestLineState.METHOD;

    StringBuilder builder = new StringBuilder();

    int currentByte;
    while ((currentByte = in.read()) != -1) {
      switch (state) {
        case METHOD:
          if (currentByte == ' ') {
            request.setMethod(builder.toString());
            builder.setLength(0);
            state = RequestLineState.PATH;
            break;
          }

          builder.append((char) currentByte);
          break;
        case PATH:
          if (currentByte == ' ') {
            request.setPath(builder.toString());
            builder.setLength(0);
            state = RequestLineState.VERSION;
            break;
          }

          builder.append((char) currentByte);
          break;
        case VERSION:
          if (currentByte == '\r') {
            // Skip \n.
            skip(1);

            request.setVersion(builder.toString());
            return true;
          }

          builder.append((char) currentByte);
          break;
      }
    }

    return false;
  }
}
