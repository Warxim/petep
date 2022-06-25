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
package com.warxim.petep.extension.internal.modifier.factory;

import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.gui.component.ConfigPane;

import java.io.IOException;

/**
 * Modifier configurator for configurating modifier data.
 */
@PetepAPI
public abstract class ModifierConfigurator extends ConfigPane<ModifierData> {
    /**
     * Constructs modifier configurator.
     * @param template Path to JavaFX template for this configurator
     * @throws IOException If the template could not be loaded
     */
    protected ModifierConfigurator(String template) throws IOException {
        super(template);
    }
}
