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
package com.warxim.petep.extension.internal.http.pdu;

import lombok.Getter;

import java.util.Arrays;

/**
 * WebSocket opcode
 */
@Getter
public enum Opcode {
    CONTINUATION(0),
    TEXT(1),
    BINARY(2),
    NON_CONTROL_1(3),
    NON_CONTROL_2(4),
    NON_CONTROL_3(5),
    NON_CONTROL_4(6),
    NON_CONTROL_5(7),
    CLOSE(8),
    PING(9),
    PONG(10),
    CONTROL_1(11),
    CONTROL_2(12),
    CONTROL_3(13),
    CONTROL_4(14),
    CONTROL_5(15);

    private final int value;

    /**
     * Constructs opcode with given value.
     * @param value Opcode value
     */
    Opcode(int value) {
        this.value = value;
    }

    /**
     * Get opcode of given value
     * @param value Number of opcode
     * @return Opcode or null if no opcode found for given value
     */
    public static Opcode valueOf(int value) {
        return Arrays.stream(Opcode.values())
                .filter(opcode -> opcode.getValue() == value)
                .findAny()
                .orElse(null);
    }
}
