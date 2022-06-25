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
package com.warxim.petep.extension.internal.common.rulegroup.intercept;

import com.warxim.petep.extension.internal.common.rulegroup.Rule;
import com.warxim.petep.extension.internal.common.rulegroup.RuleGroup;
import com.warxim.petep.extension.internal.common.rulegroup.RuleGroupManagerProvider;
import com.warxim.petep.extension.internal.common.rulegroup.config.RuleInterceptorConfig;
import com.warxim.petep.interceptor.factory.InterceptorModuleFactory;
import com.warxim.petep.interceptor.module.InterceptorModule;
import com.warxim.petep.persistence.Configurable;

/**
 * Rule interceptor module.
 * @param <R> Rule type for this interceptor module
 */
public abstract class RuleInterceptorModule<R extends Rule> extends InterceptorModule implements Configurable<RuleInterceptorConfig> {
    /**
     * Rule group that is being used by this module.
     */
    private RuleGroup<R> group;

    /**
     * Rule interceptor module constructor.
     * @param factory Factory that created this module
     * @param code Code of this module
     * @param name Name of this module
     * @param description Description of this module
     * @param enabled {@code true} if the module should be used
     */
    protected RuleInterceptorModule(
            InterceptorModuleFactory factory,
            String code,
            String name,
            String description,
            boolean enabled) {
        super(factory, code, name, description, enabled);
    }

    @Override
    public RuleInterceptorConfig saveConfig() {
        return new RuleInterceptorConfig(group.getCode());
    }

    @Override
    public void loadConfig(RuleInterceptorConfig config) {
        group = ((RuleGroupManagerProvider<R>) factory.getExtension())
                .getRuleGroupManager()
                .get(config.getRuleGroupCode())
                .orElse(null);
    }

    /**
     * Get rule group that is used by this module.
     * @return Rule group
     */
    public RuleGroup<R> getRuleGroup() {
        return group;
    }
}
