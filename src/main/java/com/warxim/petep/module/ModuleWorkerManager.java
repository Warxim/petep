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

/** Module worker manager. */
public abstract class ModuleWorkerManager<W extends ModuleWorker<?>> {
  /** Map of module workers. */
  protected Map<String, W> map;

  /** List of module workers. */
  protected List<W> list;

  /** Returns module worker by code. */
  public W get(String code) {
    return map.get(code);
  }

  /** Returns module worker list. */
  public List<W> getList() {
    return list;
  }

  /** Returns module worker map. */
  public Map<String, W> getMap() {
    return map;
  }

  /** Returns number of module workers. */
  public int size() {
    return list.size();
  }
}
