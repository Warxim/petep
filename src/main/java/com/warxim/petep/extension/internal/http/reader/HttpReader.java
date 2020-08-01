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
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.internal.http.pdu.HttpPdu;
import com.warxim.petep.extension.internal.http.pdu.HttpUtils;
import com.warxim.petep.extension.internal.http.reader.state.BodyWithChunkedEncodingState;
import com.warxim.petep.extension.internal.http.reader.state.BodyWithChunkedEncodingState.ChunkedBodyStep;
import com.warxim.petep.extension.internal.http.reader.state.BodyWithContentLengthState;
import com.warxim.petep.extension.internal.http.reader.state.InternalState;

public abstract class HttpReader extends PduReader {
  protected enum HeaderStep {
    HEADER_NAME, HEADER_VALUE
  }

  protected final int maxLength;
  protected final Charset defaultCharset;
  protected InternalState internalState;

  public HttpReader(InputStream in, int maxLength, Charset defaultCharset) {
    super(in);
    this.maxLength = maxLength;
    this.defaultCharset = defaultCharset;
  }

  public abstract PDU read() throws IOException;

  protected final boolean readHeaders(HttpPdu request) throws IOException {
    StringBuilder builder = new StringBuilder();
    HeaderStep step = HeaderStep.HEADER_NAME;
    int currentByte;
    String temp = "";
    while ((currentByte = in.read()) != -1) {
      if (step == HeaderStep.HEADER_NAME) {
        if (currentByte == ':') {
          // Skip space.
          skip(1);

          temp = HttpUtils.formatHeaderName(builder);
          builder.setLength(0);

          step = HeaderStep.HEADER_VALUE;
          continue;
        } else if (currentByte == '\r') { // End of headers.
          // Skip \n.
          skip(1);

          return true;
        }
      } else { // HEADER_VALUE
        if (currentByte == '\r') {
          // Skip \n.
          skip(1);

          String oldValue;
          if ((oldValue = request.getHeader(temp)) != null) {
            request.addHeader(temp, oldValue + ", " + builder.toString());
          } else {
            request.addHeader(temp, builder.toString());
          }
          builder.setLength(0);

          step = HeaderStep.HEADER_NAME;
          continue;
        }
      }

      builder.append((char) currentByte);
    }

    return false;
  }

  protected final void processHeaders(HttpPdu pdu) {
    // Content-Type
    String value;
    if ((value = pdu.getHeader("Content-Type")) != null) {
      int index = value.indexOf("charset=");
      if (index != -1) {
        pdu.setCharset(Charset.forName(value.substring(index + 8)));
      } else {
        pdu.setCharset(defaultCharset);
      }
    } else {
      pdu.setCharset(defaultCharset);
    }
  }

  protected final void processBodyWithContentLength(HttpPdu pdu) throws IOException {
    BodyWithContentLengthState state = ((BodyWithContentLengthState) internalState);

    byte[] buffer;
    if (state.getContentLength() > maxLength) {
      buffer = new byte[maxLength];

      if (state.isChunked()) {
        pdu.addTag("chunk");
      }

      // We are going to read just part of the message.
      state.setContentLength(state.getContentLength() - maxLength);
    } else {
      buffer = new byte[state.getContentLength()];

      if (state.isChunked()) {
        pdu.addTag("last_chunk");
      }

      // We are going to read the rest.
      internalState = null;
    }

    readNBytes(buffer, 0, buffer.length);

    pdu.setBuffer(buffer, buffer.length);
  }

  protected final void processBodyWithChunkedEncoding(HttpPdu pdu) throws IOException {
    BodyWithChunkedEncodingState state = (BodyWithChunkedEncodingState) internalState;

    int index = 0;
    byte[] buffer = new byte[maxLength];
    StringBuilder builder = new StringBuilder();
    int currentByte;

    while (index != buffer.length) {
      if (state.getChunkStep() == ChunkedBodyStep.LENGTH) {
        if ((currentByte = in.read()) == -1) {
          break;
        }

        if (currentByte == '\r') {
          // Skip \n
          skip(1);

          int length = Integer.parseInt(builder.toString(), 16);

          if (length == 0) {
            skip(2);

            pdu.setBuffer(buffer, index);
            pdu.addTag("last_chunk");

            // No more chunks.
            internalState = null;
            return;
          }

          state.setChunkLength(Integer.parseInt(builder.toString(), 16));
          state.setChunkStep(ChunkedBodyStep.CHUNK);
          builder.setLength(0);
        } else {
          builder.append((char) currentByte);
        }
      } else {
        int remaining = maxLength - index;
        if (state.getChunkLength() > remaining) {
          // Not enough space.

          // Next step is to parse the rest of the chunk.
          state.setChunkLength(state.getChunkLength() - remaining);
          state.setChunkStep(ChunkedBodyStep.CHUNK);

          // Read part of a chunk.
          readNBytes(buffer, index, remaining);
          index += remaining;

          break;
        } else {
          // Enough space.

          // Read chunk.
          readNBytes(buffer, index, state.getChunkLength());
          index += state.getChunkLength();

          // Next step is to parse length.
          state.setChunkStep(ChunkedBodyStep.LENGTH);

          // Skip \r\n
          skip(2);
        }
      }
    }

    pdu.addTag("chunk");

    pdu.setBuffer(buffer, index);
  }

  protected final void readBody(HttpPdu pdu) throws IOException {
    if (internalState == null) {
      // Determine type of body.
      String temp = pdu.getHeader("Content-Length");
      if (temp != null) {
        int length = Integer.parseInt(temp);

        internalState = new BodyWithContentLengthState(length);

        // Length is above the limit. Switching to chunked encoding, so that the PDUs will be
        // editable separately (impossible with content-length).
        if (length > maxLength) {
          pdu.removeHeader("Content-Length");
          pdu.addHeader("Transfer-Encoding", "chunked");
          ((BodyWithContentLengthState) internalState).setChunked(true);
        }
      } else if ((temp = pdu.getHeader("Transfer-Encoding")) != null && temp.equals("chunked")) {
        internalState = new BodyWithChunkedEncodingState();
      } else {
        pdu.setBuffer(new byte[0], 0);
      }
    }

    if (internalState instanceof BodyWithContentLengthState) {
      processBodyWithContentLength(pdu);
    } else if (internalState instanceof BodyWithChunkedEncodingState) {
      processBodyWithChunkedEncoding(pdu);
    }
  }

  protected final void skip(int n) throws IOException {
    if (in.skip(n) != n) {
      throw new IOException("Could not skip bytes!");
    }
  }

  protected final void readNBytes(byte[] buffer, int offset, int length) throws IOException {
    if (in.readNBytes(buffer, offset, length) != length) {
      throw new IOException("Could not skip bytes!");
    }
  }
}
