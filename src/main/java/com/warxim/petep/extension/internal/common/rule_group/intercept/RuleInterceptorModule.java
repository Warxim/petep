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
package com.warxim.petep.extension.internal.common.rule_group.intercept;

import com.warxim.petep.extension.internal.common.rule_group.Rule;
import com.warxim.petep.extension.internal.common.rule_group.RuleGroup;
import com.warxim.petep.interceptor.factory.InterceptorModuleFactory;
import com.warxim.petep.interceptor.module.InterceptorModule;

/** Rule interceptor module. */
public abstract class RuleInterceptorModule<R extends Rule> extends InterceptorModule {
  /** Rule interceptor module constructor. */
  public RuleInterceptorModule(
      InterceptorModuleFactory factory,
      String code,
      String name,
      String description,
      boolean enabled) {
    super(factory, code, name, description, enabled);
  }

  /** Returns rule group belonging to the interceptor. */
  public abstract RuleGroup<R> getRuleGroup();
}
