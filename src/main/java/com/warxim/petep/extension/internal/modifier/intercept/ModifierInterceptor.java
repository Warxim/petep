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
package com.warxim.petep.extension.internal.modifier.intercept;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.internal.common.rule_group.RuleGroup;
import com.warxim.petep.extension.internal.modifier.rule.ModifyRule;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.worker.Interceptor;

/** Modifier interceptor. */
public final class ModifierInterceptor extends Interceptor {
  private final RuleGroup<ModifyRule> group;

  /** Modifier interceptor constructor. */
  public ModifierInterceptor(int id, ModifierInterceptorModule module, PetepHelper helper) {
    super(id, module, helper);

    this.group = module.getRuleGroup();
  }

  @Override
  public boolean prepare() {
    return true;
  }

  @Override
  public boolean intercept(PDU pdu) {
    for (ModifyRule rule : group.getRules()) {
      if (!rule.process(pdu)) {
        return false;
      }
    }

    return true;
  }

  @Override
  public void stop() {
    // No action needed.
  }
}
