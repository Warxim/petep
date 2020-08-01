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

import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.PetepAPI;

/** Module factory base class. */
@PetepAPI
public abstract class ModuleFactory<M extends Module<?>> {
  protected final Extension extension;

  public ModuleFactory(Extension extension) {
    this.extension = extension;
  }

  public final Extension getExtension() {
    return extension;
  }

  /** Returns name of the factory. */
  public abstract String getName();

  /** Returns unique code of the factory. */
  public abstract String getCode();

  /** Creates module with specified parameters. */
  public abstract M createModule(String code, String name, String description, boolean enabled);
}
