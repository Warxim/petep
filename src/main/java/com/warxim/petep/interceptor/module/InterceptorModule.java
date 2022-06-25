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
package com.warxim.petep.interceptor.module;

import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.factory.InterceptorModuleFactory;
import com.warxim.petep.interceptor.worker.Interceptor;
import com.warxim.petep.module.Module;

/**
 * Interceptor module provides interceptor workers when PETEP starts.
 * <p>Is created and configured by user in application settings.</p>
 */
@PetepAPI
public abstract class InterceptorModule extends Module<InterceptorModuleFactory> {
    /**
     * Constructs interceptor module.
     * @param factory Factory that created this module
     * @param code Code of this module
     * @param name Name of this module
     * @param description Description of this module
     * @param enabled {@code true} if the module should be used
     */
    protected InterceptorModule(
            InterceptorModuleFactory factory,
            String code,
            String name,
            String description,
            boolean enabled) {
        super(factory, code, name, description, enabled);
    }

    /**
     * Creates interceptor with specified ID.
     * @param id Identifier of the interceptor (index of the interceptor)
     * @param helper Helper for allowing the interceptor to work with running PETEP core
     * @return Created interceptor
     */
    public abstract Interceptor createInterceptor(int id, PetepHelper helper);
}
