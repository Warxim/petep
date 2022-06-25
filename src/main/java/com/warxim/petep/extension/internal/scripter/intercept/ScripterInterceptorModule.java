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

import com.warxim.petep.extension.internal.common.rulegroup.intercept.RuleInterceptorModule;
import com.warxim.petep.extension.internal.scripter.rule.Script;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.factory.InterceptorModuleFactory;
import com.warxim.petep.interceptor.worker.Interceptor;

/**
 * Script interceptor module for creating interceptors.
 */
public class ScripterInterceptorModule extends RuleInterceptorModule<Script> {
    /**
     * Constructs scripter interceptor module.
     * @param factory Factory that created this module
     * @param code Code of this module
     * @param name Name of this module
     * @param description Description of this module
     * @param enabled {@code true} if the module should be used
     */
    public ScripterInterceptorModule(InterceptorModuleFactory factory,
                                     String code,
                                     String name,
                                     String description,
                                     boolean enabled) {
        super(factory, code, name, description, enabled);
    }

    @Override
    public Interceptor createInterceptor(int id, PetepHelper helper) {
        return new ScripterInterceptor(id, this, helper);
    }
}
