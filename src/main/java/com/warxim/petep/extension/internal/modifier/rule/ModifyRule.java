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
package com.warxim.petep.extension.internal.modifier.rule;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.internal.common.rule_group.Rule;
import com.warxim.petep.extension.internal.modifier.factory.Modifier;

/** Modify rule. */
public final class ModifyRule extends Rule {
  private final String tag;
  private final Modifier modifier;

  /** Modify rule constructor. */
  public ModifyRule(
      String name,
      String description,
      boolean enabled,
      String tag,
      Modifier modifier) {
    super(name, description, enabled);
    this.tag = tag;
    this.modifier = modifier;
  }

  public String getTag() {
    return tag;
  }

  public Modifier getModifier() {
    return modifier;
  }

  public boolean process(PDU pdu) {
    if ((!enabled) || (!tag.isEmpty() && !pdu.hasTag(tag))) {
      return true;
    }

    return modifier.process(pdu);
  }
}
