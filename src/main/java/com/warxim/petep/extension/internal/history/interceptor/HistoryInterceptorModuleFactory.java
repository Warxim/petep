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

import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.internal.history.HistoryApi;
import com.warxim.petep.interceptor.factory.InterceptorModuleFactory;
import com.warxim.petep.interceptor.module.InterceptorModule;

/**
 * History interceptor module factory
 */
public class HistoryInterceptorModuleFactory extends InterceptorModuleFactory {
    private final HistoryApi historyApi;

    /**
     * Constructs history interceptor module factory.
     * @param extension Extension that owns this factory
     * @param historyApi History API for saving the intercepted PDUs to the history database
     */
    public HistoryInterceptorModuleFactory(Extension extension, HistoryApi historyApi) {
        super(extension);
        this.historyApi = historyApi;
    }

    @Override
    public String getName() {
        return "History";
    }

    @Override
    public String getCode() {
        return "history";
    }

    @Override
    public InterceptorModule createModule(String code, String name, String description, boolean enabled) {
        return new HistoryInterceptorModule(this, code, name, description, enabled, historyApi);
    }
}
