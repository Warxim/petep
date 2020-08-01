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
import com.warxim.petep.extension.internal.http.pdu.WebSocketPdu;

public final class WebSocketWriter extends PduWriter {
  public WebSocketWriter(OutputStream out) {
    super(out);
  }

  public void write(PDU pdu) throws IOException {
    writeFragment((WebSocketPdu) pdu);
  }

  private void writeFragment(WebSocketPdu pdu) throws IOException {
    int currentByte = 0;

    // FIN
    if (pdu.isFinal()) {
      currentByte |= 0b1000_0000;
    }

    // RSV1
    if (pdu.isRsv1()) {
      currentByte |= 0b0100_0000;
    }

    // RSV2
    if (pdu.isRsv2()) {
      currentByte |= 0b0010_0000;
    }

    // RSV3
    if (pdu.isRsv3()) {
      currentByte |= 0b0001_0000;
    }

    // OPCODE
    currentByte |= pdu.getOpcode().getValue() & 0b0000_1111;

    out.write(currentByte);

    // MASKED
    if (pdu.isMasked()) {
      currentByte = 0b1000_0000;
    } else {
      currentByte = 0b0000_0000;
    }

    // LENGTH
    int length = pdu.getSize();
    if (length <= 125) {
      currentByte |= length & 0b0111_1111;
      out.write(currentByte);
    } else {
      currentByte |= 126;
      out.write(currentByte);

      out.write((length >> 8) & 0xFF);
      out.write(length & 0xFF);
    }

    if (pdu.isMasked()) {
      byte[] mask = pdu.getMask();

      out.write(mask, 0, 4);

      byte[] data = pdu.getBuffer();

      for (int i = 0; i < length; ++i) {
        data[i] ^= mask[i & 0x3];
      }

      out.write(data, 0, length);
    } else {
      out.write(pdu.getBuffer(), 0, length);
    }
  }
}
