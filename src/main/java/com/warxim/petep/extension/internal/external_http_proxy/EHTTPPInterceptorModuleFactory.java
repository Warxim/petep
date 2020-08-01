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
package com.warxim.petep.extension.internal.external_http_proxy;

import com.warxim.petep.extension.Extension;
import com.warxim.petep.interceptor.factory.InterceptorModuleFactory;
import com.warxim.petep.interceptor.module.InterceptorModule;

/** External HTTP Proxy interceptor module factory. */
public final class EHTTPPInterceptorModuleFactory extends InterceptorModuleFactory {
  /** External HTTP Proxy interceptor module factory constructor. */
  public EHTTPPInterceptorModuleFactory(Extension extension) {
    super(extension);
  }

  @Override
  public String getName() {
    return "External HTTP Proxy";
  }

  @Override
  public String getCode() {
    return "external_http_proxy";
  }

  @Override
  public InterceptorModule createModule(
      String code,
      String name,
      String description,
      boolean enabled) {
    return new EHTTPPInterceptorModule(this, code, name, description, enabled);
  }
}
