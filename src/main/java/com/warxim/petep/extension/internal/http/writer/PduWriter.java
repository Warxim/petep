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

import java.io.IOException;
import java.io.OutputStream;

/**
 * PDU writer for writing PDUs to output stream.
 */
public abstract class PduWriter {
    protected final OutputStream out;

    /**
     * Writes PDUs to output stream.
     * @param out Output stream to whcih to write PDUs
     */
    protected PduWriter(OutputStream out) {
        this.out = out;
    }

    /**
     * Writes PDU to output stream.
     * @param pdu PDU to write to output stream
     * @throws IOException If write fails because of various reasons
     */
    public abstract void write(PDU pdu) throws IOException;
}
