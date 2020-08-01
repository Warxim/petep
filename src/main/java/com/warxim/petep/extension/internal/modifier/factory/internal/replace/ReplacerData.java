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
package com.warxim.petep.extension.internal.modifier.factory.internal.replace;

import com.warxim.petep.extension.internal.modifier.factory.ModifierData;

public final class ReplacerData extends ModifierData {
  /** 0 = replace all, 1 .. n = replace n-th occurrence. */
  private final int occurrence;

  private final byte[] what;
  private final byte[] with;

  public ReplacerData(int occurrence, byte[] what, byte[] with) {
    this.occurrence = occurrence;
    this.what = what;
    this.with = with;
  }

  public int getOccurrence() {
    return occurrence;
  }

  public byte[] getWhat() {
    return what;
  }

  public byte[] getWith() {
    return with;
  }
}
