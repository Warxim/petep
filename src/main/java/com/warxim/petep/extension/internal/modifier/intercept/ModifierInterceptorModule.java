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

import com.warxim.petep.extension.internal.common.rule_group.RuleGroup;
import com.warxim.petep.extension.internal.common.rule_group.config.RuleInterceptorConfig;
import com.warxim.petep.extension.internal.common.rule_group.intercept.RuleInterceptorModule;
import com.warxim.petep.extension.internal.modifier.ModifierExtension;
import com.warxim.petep.extension.internal.modifier.rule.ModifyRule;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.factory.InterceptorModuleFactory;
import com.warxim.petep.interceptor.worker.Interceptor;
import com.warxim.petep.persistence.Configurable;

/** Modifier interceptor module. */
public final class ModifierInterceptorModule extends RuleInterceptorModule<ModifyRule>
    implements Configurable<RuleInterceptorConfig> {
  private RuleGroup<ModifyRule> group;

  /** Modifier interceptor module constructor. */
  public ModifierInterceptorModule(
      InterceptorModuleFactory factory,
      String code,
      String name,
      String description,
      boolean enabled) {
    super(factory, code, name, description, enabled);
  }

  @Override
  public Interceptor createInterceptor(int id, PetepHelper helper) {
    return new ModifierInterceptor(id, this, helper);
  }

  @Override
  public RuleInterceptorConfig saveConfig() {
    return new RuleInterceptorConfig(group.getCode());
  }

  @Override
  public void loadConfig(RuleInterceptorConfig config) {
    group = ((ModifierExtension) factory.getExtension()).getRuleGroupManager()
        .get(config.getRuleGroupCode());
  }

  @Override
  public RuleGroup<ModifyRule> getRuleGroup() {
    return group;
  }
}
