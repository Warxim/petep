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
import java.nio.charset.StandardCharsets;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.http.pdu.Opcode;
import com.warxim.petep.extension.internal.http.pdu.WebSocketPdu;

public final class WebSocketReader extends PduReader {
  private final PduDestination destination;

  public WebSocketReader(InputStream in, PduDestination destination) {
    super(in);
    this.destination = destination;
  }

  public WebSocketPdu read() throws IOException {
    WebSocketPdu pdu = new WebSocketPdu(null, null, destination, null, 0);

    readFragment(pdu);

    // Set UTF8 for text.
    if (pdu.getOpcode() == Opcode.TEXT) {
      pdu.setCharset(StandardCharsets.UTF_8);
    }

    return pdu;
  }

  private void readFragment(WebSocketPdu pdu) throws IOException {
    int currentByte;

    currentByte = in.read();
    if (currentByte == -1) {
      throw new IOException("Could not read first WebSocket byte.");
    }

    // FIN
    pdu.setFinal((currentByte & 0b1000_0000) != 0);

    // RSV1
    pdu.setRsv1((currentByte & 0b0100_0000) != 0);

    // RSV2
    pdu.setRsv2((currentByte & 0b0010_0000) != 0);

    // RSV3
    pdu.setRsv3((currentByte & 0b0001_0000) != 0);

    // OPCODE
    pdu.setOpcode(Opcode.valueOf(currentByte & 0b0000_1111));

    currentByte = in.read();
    if (currentByte == -1) {
      throw new IOException("Could not read second WebSocket byte.");
    }

    // MASKED
    pdu.setMasked((currentByte & 0b1000_0000) != 0);

    // LENGTH
    int length;
    currentByte = currentByte & 0b0111_1111;
    if (currentByte <= 125) {
      // It's the length!
      length = currentByte;
    } else if (currentByte == 126) {
      // The next 16 bits are the length.
      currentByte = in.read();
      if (currentByte == -1) {
        throw new IOException("Could not read WebSocket length.");
      }
      length = (currentByte & 0xFF) << 8;

      currentByte = in.read();
      if (currentByte == -1) {
        throw new IOException("Could not read WebSocket length.");
      }
      length |= (currentByte & 0xFF);
    } else {
      // The next 64 bits are the length.
      throw new IOException("WebSocket message is too large.");
    }

    byte[] data = new byte[length];

    // MASK
    if (pdu.isMasked()) {
      byte[] mask = in.readNBytes(4);

      if (mask.length != 4) {
        throw new IOException("Could not read WebSocket mask.");
      }

      pdu.setMask(mask);

      if (in.readNBytes(data, 0, length) != length) {
        throw new IOException("Could not read WebSocket data.");
      }

      for (int i = 0; i < data.length; ++i) {
        data[i] ^= mask[i & 0x3];
      }
    } else if (in.readNBytes(data, 0, length) != length) {
      throw new IOException("Could not read WebSocket data.");
    }

    pdu.setBuffer(data, length);
  }
}
