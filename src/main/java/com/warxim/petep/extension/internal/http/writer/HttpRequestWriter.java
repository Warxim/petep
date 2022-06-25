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

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.internal.http.pdu.HttpPdu;
import com.warxim.petep.extension.internal.http.pdu.HttpRequestPdu;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Http writer for writing {@link HttpRequestPdu} to output stream.
 */
public final class HttpRequestWriter extends HttpWriter {
    /**
     * Writes HTTP request PDUs to output stream.
     * @param out Output stream to whcih to write PDUs
     */
    public HttpRequestWriter(OutputStream out) {
        super(out);
    }

    @Override
    public void write(PDU request) throws IOException {
        writeRequestLine((HttpRequestPdu) request);
        writeHeaders((HttpPdu) request);
        writeBody((HttpPdu) request);
    }

    /**
     * Writes request line from PDU to output stream.
     */
    private void writeRequestLine(HttpRequestPdu request) throws IOException {
        if (request.getVersion() == null) {
            return;
        }

        out.write(request.getMethod().getBytes(StandardCharsets.ISO_8859_1));
        out.write(' ');
        out.write(request.getPath().getBytes(StandardCharsets.ISO_8859_1));
        out.write(' ');
        out.write(request.getVersion().getBytes(StandardCharsets.ISO_8859_1));
        out.write("\r\n".getBytes(StandardCharsets.ISO_8859_1));
    }
}
