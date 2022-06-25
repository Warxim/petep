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
package com.warxim.petep.extension.internal.externalhttpproxy.lighthttp;

import java.io.IOException;
import java.io.InputStream;

/**
 * Light HTTP Utils for working with streams.
 */
public final class LightHttpUtils {
    private LightHttpUtils() {
    }

    /**
     * Reads one byte from input stream.
     * @param in Input stream for reading
     * @throws IOException If the is problem with reading the input stream
     * @return One byte
     */
    public static int readByte(InputStream in) throws IOException {
        int c = in.read();

        if (c == -1) {
            throw new IOException("End of stream reached!");
        }

        return c;
    }

    /**
     * Reads data from input stream and appends it to the stringBuilder as characters until delimiter is found.
     * @param in Input stream for reading
     * @param stringBuilder String builder for appending characters
     * @param delimiter Delimiter that signalizes that no more bytes should be read
     * @throws IOException If the is problem with reading the input stream
     */
    public static void appendUntil(InputStream in, StringBuilder stringBuilder, byte delimiter) throws IOException {
        int currentByte;
        // Read target interceptor id
        while ((currentByte = LightHttpUtils.readByte(in)) != delimiter) {
            // End of stream reached.
            stringBuilder.append((char) currentByte);
        }
    }

    /**
     * Reads data from input stream and ignores it until delimiter is found.
     * @param in Input stream for reading
     * @param delimiter Delimiter that signalizes that no more bytes should be read
     * @throws IOException If the is problem with reading the input stream
     */
    public static void skipUntil(InputStream in, byte delimiter) throws IOException {
        // Read target interceptor id
        while (LightHttpUtils.readByte(in) != delimiter) {
            // Skip
        }
    }

    /**
     * Reads n bytes from input stream and ignores them.
     * @param in Input stream for reading
     * @param n Number of characters to read
     * @throws IOException If the is problem with reading the input stream
     */
    public static void skipNBytes(InputStream in, int n) throws IOException {
        if (in.skip(n) != n) {
            throw new IOException("Could not skip n bytes!");
        }
    }
}
