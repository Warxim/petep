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

/**
 * Module factory base class.
 * @param <M> Type of modules
 */
@PetepAPI
public abstract class ModuleFactory<M extends Module<?>> {
    protected final Extension extension;

    /**
     * Constructs module factory.
     * @param extension Extension that owns this factory
     */
    protected ModuleFactory(Extension extension) {
        this.extension = extension;
    }

    /**
     * Obtains parent extension of the factory.
     * @return Extension
     */
    public final Extension getExtension() {
        return extension;
    }

    /**
     * Obtains name of the factory.
     * @return Module factory name
     */
    public abstract String getName();

    /**
     * Obtains unique code of the factory.
     * @return Module factory code
     */
    public abstract String getCode();

    /**
     * Creates module with specified parameters.
     * @param code Code of the module
     * @param name Name of the module
     * @param description Description of the module
     * @param enabled Whether the module is enabled and should be used in running PETEP core
     * @return Created module
     */
    public abstract M createModule(String code, String name, String description, boolean enabled);
}
