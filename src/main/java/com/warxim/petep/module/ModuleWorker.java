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
import com.warxim.petep.helper.PetepHelper;

/**
 * Base class for module instances (workers), which are used in running PETEP core.
 * <p>Module worker represents running (working) module.</p>
 * @param <M> Type of the module
 */
@PetepAPI
public abstract class ModuleWorker<M extends Module<?>> {
    /**
     * PETEP helper for current running PETEP core instance.
     */
    protected final PetepHelper helper;

    /**
     * Parent module.
     */
    protected final M module;

    /**
     * Constructs module worker.
     * @param module Parent module of the worker
     * @param helper Helper for accessing running instance of PETEP core
     */
    protected ModuleWorker(M module, PetepHelper helper) {
        this.helper = helper;
        this.module = module;
    }

    /**
     * Obtains parent module.
     * @return Parent module
     */
    public final M getModule() {
        return module;
    }

    /**
     * Obtains PetepHelper of current PETEP core instance.
     * @return Helper for working with PETEP core
     */
    public final PetepHelper getHelper() {
        return helper;
    }

    /**
     * Obtains code of the module (represented by worker)
     * @return Module code
     */
    public String getCode() {
        return module.getCode();
    }

    /**
     * Obtains name of the module (represented by worker)
     * @return Module name
     */
    public String getName() {
        return module.getName();
    }

    /**
     * Obtains description of the module (represented by worker)
     * @return Module description
     */
    public String getDescription() {
        return module.getDescription();
    }

    @Override
    public String toString() {
        return module.toString();
    }
}
