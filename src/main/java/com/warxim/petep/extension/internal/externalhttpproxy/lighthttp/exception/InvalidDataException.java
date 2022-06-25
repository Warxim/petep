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
package com.warxim.petep.extension.internal.externalhttpproxy.lighthttp.exception;

/**
 * Invalid data exception is used for signalizing that the reader detected invalid data in the HTTP request.
 */
public class InvalidDataException extends Exception {
    private static final long serialVersionUID = 1L;
    private final byte[] message;

    /**
     * Constructs exception signalizing that invalid data has been read.
     * @param message Message describing the problem
     */
    public InvalidDataException(byte[] message) {
        this.message = message;
    }

    /**
     * Obtains message bytes.
     * @return Bytes of the message
     */
    public byte[] getMessageBytes() {
        return message;
    }
}
