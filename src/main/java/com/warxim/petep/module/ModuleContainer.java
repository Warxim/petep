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
import java.util.Optional;

/**
 * Module container base class.
 * @param <M> Type of modules
 */
public abstract class ModuleContainer<M extends Module<?>> {
    /**
     * List of modules.
     */
    protected List<M> modules;

    /**
     * Creates module container using specified module list.
     * @param modules List of modules to set into the container
     */
    protected ModuleContainer(List<M> modules) {
        this.modules = modules;
    }

    /**
     * Adds module to container.
     * @param module Module to be added
     * @return  {@code true} if the module was added;<br>
     *          {@code false} if the module code is already registered
     */
    public boolean add(M module) {
        if (contains(module.getCode())) {
            return false;
        }
        modules.add(module);
        return true;
    }

    /**
     * Obtains module by code.
     * @param code Module code to find
     * @return Module if it exists or empty optional
     */
    public Optional<M> get(String code) {
        for (var module : modules) {
            if (module.getCode().equals(code)) {
                return Optional.of(module);
            }
        }
        return Optional.empty();
    }

    /**
     * Checks whether the container contains module with specified code.
     * @param code Module code to find
     * @return {@code true} if the container contains module with given code
     */
    public boolean contains(String code) {
        for (var module : modules) {
            if (module.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Replaces old module with a new one.
     * @param oldModule Module to be replaced
     * @param newModule Module to use as replacement
     */
    public void replace(M oldModule, M newModule) {
        modules.set(modules.indexOf(oldModule), newModule);
    }

    /**
     * Removes module.
     * @param module Module to be removed
     */
    public void remove(M module) {
        modules.remove(module);
    }

    /**
     * Swaps modules.
     * @param what Index of module to be swapped
     * @param with Index of module to be swapped
     */
    public void swap(int what, int with) {
        Collections.swap(modules, what, with);
    }

    /**
     * Obtains number of modules.
     * @return Size of module list
     */
    public int size() {
        return modules.size();
    }

    /**
     * Obtains list of modules.
     * @return Unmodifiable list of modules
     */
    public List<M> getList() {
        return Collections.unmodifiableList(modules);
    }
}
