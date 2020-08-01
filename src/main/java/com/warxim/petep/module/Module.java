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

/** Module base class. */
@PetepAPI
public abstract class Module<F extends ModuleFactory<?>> {
  protected final String code;
  protected final String name;
  protected final String description;
  protected final boolean enabled;
  protected final F factory;

  /** Module constructor. */
  public Module(F factory, String code, String name, String description, boolean enabled) {
    this.code = code;
    this.name = name;
    this.description = description;
    this.enabled = enabled;
    this.factory = factory;
  }

  /*
   * GETTERS
   */
  public final boolean isEnabled() {
    return enabled;
  }

  public final String getCode() {
    return code;
  }

  public final String getName() {
    return name;
  }

  public final String getDescription() {
    return description;
  }

  public final F getFactory() {
    return factory;
  }

  @Override
  public String toString() {
    return name + " (" + code + ")";
  }
}
