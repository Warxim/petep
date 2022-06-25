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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Module manager base class.
 * <p>Uses thread safe collections, because registration of factories can run in parallel.</p>
 * @param <F> Type of module factories
 */
public abstract class ModuleFactoryManager<F extends ModuleFactory<?>> {
    /**
     * Map of factories (key = code, value = factory).
     */
    private final Map<String, F> factoryMap;

    /**
     * List of factories.
     */
    private final List<F> factoryList;

    /**
     * Module manager constructor.
     */
    protected ModuleFactoryManager() {
        factoryMap = new ConcurrentHashMap<>();
        factoryList = new CopyOnWriteArrayList<>();
    }

    /**
     * Registers module factory.
     * @param factory Factory to be registered
     * @return  {@code true} if the factory was successfully registered;<br>
     *          {@code false} if the factory code is already registered
     */
    public boolean registerModuleFactory(F factory) {
        if (factoryMap.putIfAbsent(factory.getCode(), factory) == null) {
            factoryList.add(factory);
            return true;
        }
        return false;
    }

    /**
     * Unregisters module factory.
     * @param factory Factory to be unregistered
     * @return {@code true} if the module factory was removed (was present before)
     */
    public boolean unregisterModuleFactory(F factory) {
        factoryMap.remove(factory.getCode());
        return factoryList.remove(factory);
    }

    /**
     * Obtains map of module factories (key = code, value = factory).
     * @return Unmodifiable map of factories mapped by code
     */
    public Map<String, F> getMap() {
        return Collections.unmodifiableMap(factoryMap);
    }

    /**
     * Obtains list of module factories.
     * @return Unmodifiable list of factories
     */
    public List<F> getList() {
        return Collections.unmodifiableList(factoryList);
    }

    /**
     * Obtains factory by code.
     * @param code Code of the factory
     * @return Factory or empty optional if it does not exist
     */
    public Optional<F> getModuleFactory(String code) {
        return Optional.ofNullable(factoryMap.get(code));
    }
}
