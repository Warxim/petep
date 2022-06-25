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
package com.warxim.petep.extension.internal.logger;

import com.warxim.petep.extension.Extension;
import com.warxim.petep.gui.component.ConfigPane;
import com.warxim.petep.interceptor.factory.InterceptorModuleFactory;
import com.warxim.petep.interceptor.module.InterceptorModule;
import com.warxim.petep.persistence.Configurator;

import java.io.IOException;

/**
 * Logger interceptor module factory.
 */
public final class LoggerInterceptorModuleFactory extends InterceptorModuleFactory
        implements Configurator<LoggerConfig> {
    /**
     * Logger interceptor module factory constructor.
     * @param extension Extension that owns this factory
     */
    public LoggerInterceptorModuleFactory(Extension extension) {
        super(extension);
    }

    @Override
    public String getName() {
        return "Logger";
    }

    @Override
    public String getCode() {
        return "logger";
    }

    @Override
    public InterceptorModule createModule(String code, String name, String description, boolean enabled) {
        return new LoggerInterceptorModule(this, code, name, description, enabled);
    }

    @Override
    public ConfigPane<LoggerConfig> createConfigPane() throws IOException {
        return new LoggerConfigurator();
    }
}
