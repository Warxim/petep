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
package com.warxim.petep.module;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/** Module manager base class. */
public abstract class ModuleFactoryManager<F extends ModuleFactory<?>> {
  /** Map of factories (key = code, value = factory). */
  private final Map<String, F> factoryMap;

  /** List of factories. */
  private final List<F> factoryList;

  /** Module manager constructor. */
  public ModuleFactoryManager() {
    factoryMap = new ConcurrentHashMap<>();
    factoryList = new CopyOnWriteArrayList<>();
  }

  /** Registers module. */
  public boolean registerModuleFactory(F factory) {
    if (factoryMap.putIfAbsent(factory.getCode(), factory) == null) {
      factoryList.add(factory);
      return true;
    }
    return false;
  }

  /** Unregisters module. */
  public boolean unregisterModuleFactory(F module) {
    factoryMap.remove(module.getCode());
    return factoryList.remove(module);
  }

  /** Returns map of module factories (key = code, value = factory). */
  public Map<String, F> getMap() {
    return factoryMap;
  }

  /** Returns list of module factories. */
  public List<F> getList() {
    return factoryList;
  }

  /** Returns factory by code. */
  public F getModuleFactory(String code) {
    return factoryMap.get(code);
  }
}
