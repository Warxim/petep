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
import com.warxim.petep.extension.internal.http.pdu.HttpRequestPdu;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Http reader for reading requests.
 */
public final class HttpRequestReader extends HttpReader {
    /**
     * Constructs HTTP request PDU reader.
     * @param in Input stream for reading the data
     * @param maxLength Maximal length of PDU body
     * @param defaultCharset Default charset to set to PDUs
     */
    public HttpRequestReader(InputStream in, int maxLength, Charset defaultCharset) {
        super(in, maxLength, defaultCharset);
    }

    @Override
    public HttpRequestPdu read() throws IOException {
        var request = new HttpRequestPdu(null, null, PduDestination.SERVER, null, 0, Constant.DEFAULT_CHARSET);

        if (internalBodyState == null) {
            if (!readRequestLine(request) || !readHeaders(request)) {
                return null;
            }

            processHeaders(request);
        }

        readBody(request);

        return request;
    }

    /**
     * Reads request line.
     */
    private boolean readRequestLine(HttpRequestPdu request) throws IOException {
        var state = RequestLineState.METHOD;
        var builder = new StringBuilder();
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

    /**
     * Request line reading step (what are we reading now).
     */
    protected enum RequestLineState {
        METHOD, PATH, VERSION
    }
}
