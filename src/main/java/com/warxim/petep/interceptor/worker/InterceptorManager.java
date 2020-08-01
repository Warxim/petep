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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.module.InterceptorModule;
import com.warxim.petep.interceptor.module.InterceptorModuleContainer;
import com.warxim.petep.module.ModuleWorkerManager;

/** Interceptor manager. */
public final class InterceptorManager extends ModuleWorkerManager<Interceptor> {
  public InterceptorManager(PetepHelper helper, InterceptorModuleContainer container) {
    Map<String, Interceptor> tempMap = new HashMap<>((int) (container.size() / 0.75) + 1, 0.75f);
    List<Interceptor> tempList = new ArrayList<>(container.size());

    // Create interceptors using modules.
    for (InterceptorModule module : container.getList()) {
      // Skip disabled interceptor.
      if (!module.isEnabled()) {
        continue;
      }

      // Create interceptor.
      Interceptor interceptor = module.createInterceptor(tempList.size(), helper);
      tempList.add(interceptor);
      tempMap.put(module.getCode(), interceptor);
    }

    // Create unmodifiable collections.
    map = Collections.unmodifiableMap(tempMap);
    list = Collections.unmodifiableList(tempList);
  }
}
