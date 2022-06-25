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
import com.warxim.petep.extension.internal.http.pdu.HttpResponsePdu;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Http writer for writing {@link HttpResponsePdu} to output stream.
 */
public final class HttpResponseWriter extends HttpWriter {
    /**
     * Writes HTTP response PDUs to output stream.
     * @param out Output stream to whcih to write PDUs
     */
    public HttpResponseWriter(OutputStream out) {
        super(out);
    }

    @Override
    public void write(PDU response) throws IOException {
        writeStatusLine((HttpResponsePdu) response);
        writeHeaders((HttpPdu) response);
        writeBody((HttpPdu) response);
    }

    /**
     * Writes status line from PDU to output stream.
     */
    private void writeStatusLine(HttpResponsePdu response) throws IOException {
        if (response.getVersion() == null) {
            return;
        }

        out.write(response.getVersion().getBytes(StandardCharsets.ISO_8859_1));
        out.write(' ');
        out.write(String.valueOf(response.getStatusCode()).getBytes(StandardCharsets.ISO_8859_1));
        out.write(' ');
        out.write(response.getStatusMessage().getBytes(StandardCharsets.ISO_8859_1));
        out.write("\r\n".getBytes(StandardCharsets.ISO_8859_1));
    }
}
