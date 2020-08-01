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
package com.warxim.petep.proxy.worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.module.ModuleWorkerManager;
import com.warxim.petep.proxy.module.ProxyModule;
import com.warxim.petep.proxy.module.ProxyModuleContainer;

/** Proxy manager. */
public final class ProxyManager extends ModuleWorkerManager<Proxy> {
  public ProxyManager(PetepHelper helper, ProxyModuleContainer container) {
    Map<String, Proxy> tempMap = new HashMap<>((int) (container.size() / 0.75) + 1, 0.75f);
    List<Proxy> tempList = new ArrayList<>(container.size());

    // Create proxies using modules.
    for (ProxyModule module : container.getList()) {
      if (!module.isEnabled()) {
        continue;
      }

      // Create proxy and add it to collections.
      Proxy proxy = module.createProxy(helper);
      tempList.add(proxy);
      tempMap.put(module.getCode(), proxy);
    }

    // Create unmodifiable collections.
    map = Collections.unmodifiableMap(tempMap);
    list = Collections.unmodifiableList(tempList);
  }
}
