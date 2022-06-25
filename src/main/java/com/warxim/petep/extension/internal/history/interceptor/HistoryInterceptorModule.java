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
package com.warxim.petep.extension.internal.history.interceptor;

import com.warxim.petep.extension.internal.history.HistoryApi;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.factory.InterceptorModuleFactory;
import com.warxim.petep.interceptor.module.InterceptorModule;
import com.warxim.petep.interceptor.worker.Interceptor;

/**
 * History interceptor module
 */
public class HistoryInterceptorModule extends InterceptorModule {
    private final HistoryApi historyApi;

    /**
     * Constructs history interceptor module.
     * @param factory Factory that created this module
     * @param code Code of this module
     * @param name Name of this module
     * @param description Description of this module
     * @param enabled {@code true} if the module should be used
     * @param historyApi History API for saving the intercepted PDUs to the history database
     */
    public HistoryInterceptorModule(InterceptorModuleFactory factory,
                                    String code,
                                    String name,
                                    String description,
                                    boolean enabled,
                                    HistoryApi historyApi) {
        super(factory, code, name, description, enabled);
        this.historyApi = historyApi;
    }

    @Override
    public Interceptor createInterceptor(int id, PetepHelper helper) {
        return new HistoryInterceptor(id, this, helper, historyApi);
    }
}
