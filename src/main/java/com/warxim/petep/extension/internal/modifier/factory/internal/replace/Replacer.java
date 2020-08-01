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

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.internal.modifier.factory.Modifier;
import com.warxim.petep.extension.internal.modifier.factory.ModifierData;
import com.warxim.petep.extension.internal.modifier.factory.ModifierFactory;
import com.warxim.petep.util.PduUtils;

public final class Replacer extends Modifier {
  public Replacer(ModifierFactory factory, ModifierData data) {
    super(factory, data);
  }

  @Override
  public boolean process(PDU pdu) {
    ReplacerData rule = (ReplacerData) data;

    // run replace
    if (rule.getOccurrence() == -1) {
      PduUtils.replace(pdu, rule.getWhat(), rule.getWith());
    } else {
      PduUtils.replace(pdu, rule.getWhat(), rule.getWith(), rule.getOccurrence());
    }

    return true;
  }
}
