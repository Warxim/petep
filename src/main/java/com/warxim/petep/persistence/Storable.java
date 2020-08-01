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
package com.warxim.petep.persistence;

import com.warxim.petep.extension.PetepAPI;

/**
 * Allows extensions and modules to define their own data stores that are serialized to JSON
 * structure and saved to project files.
 */
@PetepAPI
public interface Storable<S> {
  /** Returns store to be saved. */
  S saveStore();

  /** Loads store. */
  void loadStore(S store);
}
