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
package com.warxim.petep.extension;

import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.GuiHelper;

/** Superclass for extensions. */
@PetepAPI
public abstract class Extension {
  /** Path where the extension .jar file is located. */
  protected final String path;

  public Extension(String path) {
    this.path = path;
  }

  public final String getPath() {
    return path;
  }

  /** Initializes the extension. */
  public abstract void init(ExtensionHelper helper);

  /** Initializes the extension GUI. */
  public abstract void initGui(GuiHelper helper);

  public abstract String getCode();

  public abstract String getName();

  public abstract String getDescription();

  public abstract String getVersion();
}
