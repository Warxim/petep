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

public enum Opcode {
  CONTINUATION(0), TEXT(1), BINARY(2), NON_CONTROL_1(3), NON_CONTROL_2(4), NON_CONTROL_3(
      5), NON_CONTROL_4(6), NON_CONTROL_5(7), CLOSE(8), PING(
          9), PONG(10), CONTROL_1(11), CONTROL_2(12), CONTROL_3(13), CONTROL_4(14), CONTROL_5(15);

  private final int value;

  Opcode(int value) {
    this.value = value;
  }

  public final int getValue() {
    return value;
  }

  public static final Opcode valueOf(int value) {
    switch (value) {
      case 0:
        return Opcode.CONTINUATION;
      case 1:
        return Opcode.TEXT;
      case 2:
        return Opcode.BINARY;
      case 3:
        return Opcode.NON_CONTROL_1;
      case 4:
        return Opcode.NON_CONTROL_2;
      case 5:
        return Opcode.NON_CONTROL_3;
      case 6:
        return Opcode.NON_CONTROL_4;
      case 7:
        return Opcode.NON_CONTROL_5;
      case 8:
        return Opcode.CLOSE;
      case 9:
        return Opcode.PING;
      case 10:
        return Opcode.PONG;
      case 11:
        return Opcode.CONTROL_1;
      case 12:
        return Opcode.CONTROL_2;
      case 13:
        return Opcode.CONTROL_3;
      case 14:
        return Opcode.CONTROL_4;
      case 15:
        return Opcode.CONTROL_5;
      default:
        return null;
    }
  }
}
