/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2021 Michal Válka
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

import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.internal.common.rulegroup.Rule;
import com.warxim.petep.extension.internal.common.rulegroup.RuleGroupManagerProvider;
import com.warxim.petep.extension.internal.common.rulegroup.config.RuleInterceptorConfig;
import com.warxim.petep.extension.internal.common.rulegroup.gui.RuleInterceptorConfigurator;
import com.warxim.petep.gui.component.ConfigPane;
import com.warxim.petep.interceptor.factory.InterceptorModuleFactory;
import com.warxim.petep.persistence.Configurator;

import java.io.IOException;

/**
 * Rule interceptor module factory .
 * @param <R> Rule type for this interceptor module factory
 */
public abstract class RuleInterceptorModuleFactory<R extends Rule> extends InterceptorModuleFactory implements Configurator<RuleInterceptorConfig> {
    /**
     * Constructs rule interceptor module factory
     * @param extension Extension that owns this factory
     */
    protected RuleInterceptorModuleFactory(Extension extension) {
        super(extension);
    }

    @Override
    public ConfigPane<RuleInterceptorConfig> createConfigPane() throws IOException {
        return new RuleInterceptorConfigurator<>(((RuleGroupManagerProvider<R>) extension).getRuleGroupManager());
    }
}
