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
package com.warxim.petep.extension.internal.modifier.factory;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.PetepAPI;

@PetepAPI
public abstract class Modifier {
  protected final ModifierFactory factory;

  protected ModifierData data;

  public Modifier(ModifierFactory factory, ModifierData data) {
    this.factory = factory;
    this.data = data;
  }

  public ModifierFactory getFactory() {
    return factory;
  }

  public ModifierData getData() {
    return data;
  }

  /**
   * Processes PDU in the modifier.
   *
   * @return FALSE if PDU should be dropped.
   */
  public abstract boolean process(PDU pdu);

  @Override
  public String toString() {
    return factory.getName();
  }
}
