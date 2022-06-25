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

import com.warxim.petep.core.pdu.PDU;

import java.io.IOException;
import java.io.InputStream;

/**
 * Reader of PDUs for HTTP extension.
 */
public abstract class PduReader {
    protected final InputStream in;

    /**
     * Constructs PDU reader.
     * @param in Input stream for reading the data
     */
    protected PduReader(InputStream in) {
        this.in = in;
    }

    /**
     * Reads PDU from input stream.
     * @return Read PDU
     * @throws IOException If read fails because of various reasons
     */
    public abstract PDU read() throws IOException;
}
