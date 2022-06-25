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

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Modifier factory for generating modifier rules.
 */
@PetepAPI
public abstract class ModifierFactory {
    /**
     * Gets factory code
     * @return Factory code (for configuration purposes)
     */
    public abstract String getCode();

    /**
     * Gets factory name
     * @return Factory name (visible for user)
     */
    public abstract String getName();

    /**
     * Creates modifier using given data.
     * @param data Data for the modifier
     * @return New modifier instance
     */
    public abstract Modifier createModifier(ModifierData data);

    /**
     * Get type of modifier configuration
     * @return Type of configuration for deserialization from JSON or empty optional if no configuration is needed
     */
    public abstract Optional<Type> getConfigType();

    /**
     * Creates config pane for modifier data.
     * @throws IOException If there has been problem with loading FXML template
     * @return Modifier configurator for configuring modifier data or empty optional if no configurator is needed
     */
    public abstract Optional<ModifierConfigurator> createConfigPane() throws IOException;

    @Override
    public String toString() {
        return getName();
    }
}
