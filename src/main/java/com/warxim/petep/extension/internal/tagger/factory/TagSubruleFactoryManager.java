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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tag subrule factory manager.
 * <p>Manages tag subrule factory registration.</p>
 */
public final class TagSubruleFactoryManager {
    private final Map<String, TagSubruleFactory> factories;

    /**
     * Tag subrule factory manager constructor.
     */
    public TagSubruleFactoryManager() {
        factories = new ConcurrentHashMap<>();
    }

    /**
     * Registers tag subrule factory
     * @param factory Tag subrule factory to be registered
     * @return {@code true} if the factory has been successfully registered ({@code false} if the code is taken)
     */
    public boolean registerFactory(TagSubruleFactory factory) {
        return factories.putIfAbsent(factory.getCode(), factory) == null;
    }

    /**
     * Obtains factory by code
     * @param code Code of the factory
     * @return Tag subrule factory or empty optional if it does not exist
     */
    public Optional<TagSubruleFactory> getFactory(String code) {
        return Optional.ofNullable(factories.get(code));
    }

    /**
     * Gets all registered factories
     * @return List of tag subrule factories
     */
    public List<TagSubruleFactory> getFactories() {
        return new ArrayList<>(factories.values());
    }
}
