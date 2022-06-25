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
package com.warxim.petep.interceptor.worker;

import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.module.InterceptorModuleContainer;
import com.warxim.petep.module.ModuleWorkerManager;

/**
 * Interceptor manager.
 * <p>Manages active interceptors for running core.</p>
 */
public final class InterceptorManager extends ModuleWorkerManager<Interceptor> {
    /**
     * Constructs interceptor manager.
     * <p>Creates list and map of interceptor workers by generating workers using enabled modules.</p>
     * @param container Container containing modules for generation
     * @param helper PETEP helper for currently running core
     */
    public InterceptorManager(
            PetepHelper helper,
            InterceptorModuleContainer container) {
        super(container, ((module, index) -> module.createInterceptor(index, helper)));
    }
}
