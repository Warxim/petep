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

import java.util.*;
import java.util.function.BiFunction;

/**
 * Module worker manager.
 * <p>Manages module workers (instances representing modules in running PETEP core).</p>
 * @param <W> Type of module workers
 */
public abstract class ModuleWorkerManager<W extends ModuleWorker<?>> {
    /**
     * Map of module workers.
     */
    protected Map<String, W> map;

    /**
     * List of module workers.
     */
    protected List<W> list;

    /**
     * Constructs module worker manager.
     * <p>Creates list and map of workers by generating workers using enabled modules.</p>
     * @param container Container containing modules for generation
     * @param workerGenerator Generates worker using given module and index
     */
    protected <M extends Module<?>> ModuleWorkerManager(
            ModuleContainer<M> container,
            BiFunction<M, Integer, W> workerGenerator) {
        var tempMap = new HashMap<String, W>((int) (container.size() / 0.75) + 1, 0.75f);
        var tempList = new ArrayList<W>(container.size());

        // Create workers using modules.
        for (var module : container.getList()) {
            if (!module.isEnabled()) {
                continue;
            }

            // Create worker and add it to collections.
            var worker = workerGenerator.apply(module, tempList.size());
            tempList.add(worker);
            tempMap.put(module.getCode(), worker);
        }

        // Create unmodifiable collections.
        map = Collections.unmodifiableMap(tempMap);
        list = Collections.unmodifiableList(tempList);
    }

    /**
     * Obtains module worker by code.
     * @param code Code of the worker (module)
     * @return Module worker
     */
    public W get(String code) {
        return map.get(code);
    }

    /**
     * Obtains module worker list.
     * @return List of module workers
     */
    public List<W> getList() {
        return list;
    }

    /**
     * Obtains module worker map.
     * @return Map of module workers mapped by code
     */
    public Map<String, W> getMap() {
        return map;
    }

    /**
     * Obtains number of module workers.
     * @return Size of the module worker list
     */
    public int size() {
        return list.size();
    }
}
