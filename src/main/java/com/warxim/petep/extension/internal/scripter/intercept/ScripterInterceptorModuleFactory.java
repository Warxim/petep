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
package com.warxim.petep.extension.internal.scripter.intercept;

import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.internal.common.rulegroup.intercept.RuleInterceptorModuleFactory;
import com.warxim.petep.extension.internal.scripter.rule.Script;
import com.warxim.petep.interceptor.module.InterceptorModule;

/**
 * Script interceptor module factory for creating interceptor modules.
 */
public class ScripterInterceptorModuleFactory extends RuleInterceptorModuleFactory<Script> {
    /**
     * Constructs scripter interceptor module factory.
     * @param extension Extension that owns this factory
     */
    public ScripterInterceptorModuleFactory(Extension extension) {
        super(extension);
    }

    @Override
    public String getName() {
        return "Scripter";
    }

    @Override
    public String getCode() {
        return "scripter";
    }

    @Override
    public InterceptorModule createModule(String code, String name, String description, boolean enabled) {
        return new ScripterInterceptorModule(this, code, name, description, enabled);
    }
}
