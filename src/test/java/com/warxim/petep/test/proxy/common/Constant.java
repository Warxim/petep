/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2021 Michal Válka
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
package com.warxim.petep.test.proxy.common;

import static com.warxim.petep.test.base.util.TestUtils.generateAllBytes;
import static com.warxim.petep.test.base.util.TestUtils.generateBytes;

public final class Constant {
    public static final int BUFFER_SIZE = 4096;

    public static final int PARALLEL_CONNECTION_COUNT = 25;

    public static final byte[] DATA_1 = new byte[] { 0x00 };
    public static final byte[] DATA_2 = "Some data...".getBytes();
    public static final byte[] DATA_3 = generateAllBytes();
    public static final byte[] DATA_4 = new byte[] { 0x00, 0x01, 0x02 };
    public static final byte[] DATA_5 = generateBytes(BUFFER_SIZE);

    public static final Message MESSAGE_1 = new Message(DATA_1, DATA_1.length);
    public static final Message MESSAGE_2 = new Message(DATA_2, DATA_2.length);
    public static final Message MESSAGE_3 = new Message(DATA_3, DATA_3.length);
    public static final Message MESSAGE_4 = new Message(DATA_4, DATA_4.length);
    public static final Message MESSAGE_5 = new Message(DATA_5, DATA_5.length);

    private Constant() {
    }
}
