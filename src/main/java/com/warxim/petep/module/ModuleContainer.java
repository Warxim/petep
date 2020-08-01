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

import java.util.Collections;
import java.util.List;

/** Module container base class. */
public abstract class ModuleContainer<M extends Module<?>> {
  /** List of modules. */
  protected List<M> modules;

  /** Creates module container using specified module list. */
  public ModuleContainer(List<M> modules) {
    this.modules = modules;
  }

  /** Adds module to manager. */
  public void add(M module) {
    modules.add(module);
  }

  /** Returns module by code. */
  public M get(String code) {
    for (M module : modules) {
      if (module.getCode().equals(code)) {
        return module;
      }
    }
    return null;
  }

  /** Returns true if manager contains module with specified code. */
  public boolean contains(String code) {
    for (M module : modules) {
      if (module.getCode().equals(code)) {
        return true;
      }
    }
    return false;
  }

  /** Replaces module with a new one. */
  public void replace(M oldModule, M newModule) {
    modules.set(modules.indexOf(oldModule), newModule);
  }

  /** Removes module with given code. */
  public void remove(M module) {
    modules.remove(module);
  }

  /** Swaps modules. */
  public void swap(int what, int with) {
    Collections.swap(modules, what, with);
  }

  /** Returns number of modules. */
  public int size() {
    return modules.size();
  }

  /** Returns list of modules. */
  public List<M> getList() {
    return modules;
  }
}
