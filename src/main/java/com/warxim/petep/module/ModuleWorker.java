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

import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.helper.PetepHelper;

/** Instance base class. */
@PetepAPI
public abstract class ModuleWorker<M extends Module<?>> {
  /** PETEP helper for current running PETEP core instance. */
  protected final PetepHelper helper;

  /** Parent module. */
  protected final M module;

  public ModuleWorker(M module, PetepHelper helper) {
    this.helper = helper;
    this.module = module;
  }

  /** Returns parent module. */
  public final M getModule() {
    return module;
  }

  /** Returns PetepHelper of current PETEP core instance. */
  public final PetepHelper getHelper() {
    return helper;
  }

  @Override
  public String toString() {
    return module.toString();
  }
}
