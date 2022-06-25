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

import com.warxim.petep.common.Constant;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.http.pdu.Opcode;
import com.warxim.petep.extension.internal.http.pdu.WebSocketPdu;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * PDU reader for reading WebSocket PDUs from stream.
 */
public final class WebSocketReader extends PduReader {
    private final PduDestination destination;

    /**
     * Constructs WebSocket PDU reader.
     * @param in Input stream for reading the data
     * @param destination Destination for the read PDUs
     */
    public WebSocketReader(InputStream in, PduDestination destination) {
        super(in);
        this.destination = destination;
    }

    @Override
    public WebSocketPdu read() throws IOException {
        var pdu = new WebSocketPdu(null, null, destination, null, 0, Constant.DEFAULT_CHARSET);

        readFragment(pdu);

        // Set UTF8 for text.
        if (pdu.getOpcode() == Opcode.TEXT) {
            pdu.setCharset(StandardCharsets.UTF_8);
        }

        return pdu;
    }

    /**
     * Reads WebSocket fragment.
     */
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

        // LENGTH & CONTENT
        readLengthAndContent(currentByte, pdu);
    }

    /**
     * Reads content.
     */
    private void readLengthAndContent(int currentByte, WebSocketPdu pdu) throws IOException {
        int length = readLength(currentByte);

        var data = new byte[length];

        // MASK
        if (pdu.isMasked()) {
            var mask = in.readNBytes(4);

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

    /**
     * Reads length from byte.
     */
    private int readLength(int currentByte) throws IOException {
        currentByte = currentByte & 0b0111_1111;
        if (currentByte <= 125) {
            // It's the length!
            return currentByte;
        } else if (currentByte == 126) {
            // The next 16 bits are the length.
            currentByte = in.read();
            if (currentByte == -1) {
                throw new IOException("Could not read WebSocket length.");
            }
            int length = (currentByte & 0xFF) << 8;
            currentByte = in.read();
            if (currentByte == -1) {
                throw new IOException("Could not read WebSocket length.");
            }
            length |= (currentByte & 0xFF);
            return length;
        }
        // The next 64 bits are the length.
        throw new IOException("WebSocket message is too large.");
    }
}
