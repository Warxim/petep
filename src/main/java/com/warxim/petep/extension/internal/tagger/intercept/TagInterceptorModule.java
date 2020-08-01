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
package com.warxim.petep.extension.internal.tagger.intercept;

import com.warxim.petep.extension.internal.common.rule_group.RuleGroup;
import com.warxim.petep.extension.internal.common.rule_group.config.RuleInterceptorConfig;
import com.warxim.petep.extension.internal.common.rule_group.intercept.RuleInterceptorModule;
import com.warxim.petep.extension.internal.tagger.TaggerExtension;
import com.warxim.petep.extension.internal.tagger.rule.TagRule;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.factory.InterceptorModuleFactory;
import com.warxim.petep.interceptor.worker.Interceptor;
import com.warxim.petep.persistence.Configurable;

/** Tag interceptor module. */
public final class TagInterceptorModule extends RuleInterceptorModule<TagRule>
    implements Configurable<RuleInterceptorConfig> {
  private RuleGroup<TagRule> group;

  /** Tag interceptor module constructor. */
  public TagInterceptorModule(
      InterceptorModuleFactory factory,
      String code,
      String name,
      String description,
      boolean enabled) {
    super(factory, code, name, description, enabled);
  }

  @Override
  public Interceptor createInterceptor(int id, PetepHelper helper) {
    return new TagInterceptor(id, this, helper);
  }

  @Override
  public RuleInterceptorConfig saveConfig() {
    return new RuleInterceptorConfig(group.getCode());
  }

  @Override
  public void loadConfig(RuleInterceptorConfig config) {
    group = ((TaggerExtension) factory.getExtension()).getRuleGroupManager()
        .get(config.getRuleGroupCode());
  }

  @Override
  public RuleGroup<TagRule> getRuleGroup() {
    return group;
  }
}
