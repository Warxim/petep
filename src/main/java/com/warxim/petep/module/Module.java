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
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Module base class.
 * @param <F> Type of parent factory of the module
 */
@Getter
@AllArgsConstructor
@PetepAPI
public abstract class Module<F extends ModuleFactory<?>> {
    /**
     * Parent factory, which produced this module
     */
    protected final F factory;

    /**
     * Unique code of the module
     */
    protected final String code;

    /**
     * Name of the module for displaying in the GUI
     */
    protected final String name;

    /**
     * Description of the module
     */
    protected final String description;

    /**
     * Whether the module is enabled and should be used in running PETEP core or not
     */
    protected final boolean enabled;

    @Override
    public String toString() {
        return name + " (" + code + ")";
    }
}
