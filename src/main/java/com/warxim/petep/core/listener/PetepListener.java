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
package com.warxim.petep.core.listener;

import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.helper.PetepHelper;

/**
 * PETEP listener interface. Allows extensions to listen for PETEP events and also allows them to obtain PetepHelper.
 * <p>
 *    <b>Warning #1</b>: Extensions should remove PetepHelper from their memory after PETEP stops, so that GC can collect resources.
 *    (This, of course, applies to all other resources that are connected to PETEP instance.)
 * </p>
 * <p>
 *    <b>Warning #2</b>: Listeners are called in parallel, so be careful when accessing the same data in two listeners.
 * </p>
 */
@PetepAPI
public interface PetepListener {
    /**
     * Event before prepare step is processed.
     * @param helper PETEP helper for currently running core
     */
    default void beforeCorePrepare(PetepHelper helper) {
    }

    /**
     * Event after prepare step is processed.
     * @param helper PETEP helper for currently running core
     */
    default void afterCorePrepare(PetepHelper helper) {
    }

    /**
     * Event before start step is processed.
     * @param helper PETEP helper for currently running core
     */
    default void beforeCoreStart(PetepHelper helper) {
    }

    /**
     * Event after start step is processed.
     * @param helper PETEP helper for currently running core
     */
    default void afterCoreStart(PetepHelper helper) {
    }

    /**
     * Event before stop step is processed.
     * @param helper PETEP helper for currently running core
     */
    default void beforeCoreStop(PetepHelper helper) {
    }

    /**
     * Event after stop step is processed.
     * @param helper PETEP helper for currently running core
     */
    default void afterCoreStop(PetepHelper helper) {
    }
}
