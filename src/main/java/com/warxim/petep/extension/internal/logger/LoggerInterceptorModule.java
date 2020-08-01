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

import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.module.InterceptorModule;
import com.warxim.petep.interceptor.worker.Interceptor;
import com.warxim.petep.persistence.Configurable;

/** Logger interceptor module. */
public final class LoggerInterceptorModule extends InterceptorModule
    implements Configurable<LoggerConfig> {
  private LoggerConfig config;

  /** Logger interceptor module constructor. */
  public LoggerInterceptorModule(
      LoggerInterceptorModuleFactory factory,
      String code,
      String name,
      String description,
      boolean enabled) {
    super(factory, code, name, description, enabled);
  }

  @Override
  public LoggerConfig saveConfig() {
    return config;
  }

  @Override
  public void loadConfig(LoggerConfig config) {
    this.config = config;
  }

  @Override
  public Interceptor createInterceptor(int id, PetepHelper helper) {
    return new LoggerInterceptor(id, this, helper);
  }

  public LoggerConfig getConfig() {
    return config;
  }
}
