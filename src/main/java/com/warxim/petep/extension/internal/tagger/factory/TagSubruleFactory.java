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
package com.warxim.petep.extension.internal.tagger.factory;

import com.warxim.petep.extension.PetepAPI;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Tag subrule factory.
 */
@PetepAPI
public abstract class TagSubruleFactory {
    /**
     * Obtains factory code (for configuration purposes).
     * @return Factory code for identification of the factory
     */
    public abstract String getCode();

    /**
     * Obtains factory name (visible for user).
     * @return Factory name for displaying in GUI
     */
    public abstract String getName();

    /**
     * Creates subrule using given data.
     * @param data Data for the subrule
     * @return Created tag subrule
     */
    public abstract TagSubrule createSubrule(TagSubruleData data);

    /**
     * Obtains type of configuration, so it can be deserialized from JSON configuration.
     * @return Type of configuration for deserialization from JSON or empty optional if no configuration is needed
     */
    public abstract Optional<Type> getConfigType();

    /**
     * Creates config pane for subrule data.
     * @return Tag subrule configurator for configuring modifier data or empty optional if no configurator is needed
     * @throws IOException If there was problem with loading the configuration pane
     */
    public abstract Optional<TagSubruleConfigurator> createConfigPane() throws IOException;

    @Override
    public String toString() {
        return getName();
    }
}
