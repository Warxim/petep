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

import com.warxim.petep.helper.PetepHelper;

/**
 * Listener manager that allows modules to register their own listener.
 * <p>
 *     Calls listeners in parallel.
 * </p>
 * <p>
 *     Based on {@link ListenerManager}
 * </p>
 */
public final class PetepListenerManager extends ListenerManager<PetepListener> implements PetepListener {
    @Override
    public void beforeCorePrepare(PetepHelper helper) {
        parallelCall(listener -> listener.beforeCorePrepare(helper));
    }

    @Override
    public void afterCorePrepare(PetepHelper helper) {
        parallelCall(listener -> listener.afterCorePrepare(helper));
    }

    @Override
    public void beforeCoreStart(PetepHelper helper) {
        parallelCall(listener -> listener.beforeCoreStart(helper));
    }

    @Override
    public void afterCoreStart(PetepHelper helper) {
        parallelCall(listener -> listener.afterCoreStart(helper));
    }

    @Override
    public void beforeCoreStop(PetepHelper helper) {
        parallelCall(listener -> listener.beforeCoreStop(helper));
    }

    @Override
    public void afterCoreStop(PetepHelper helper) {
        parallelCall(listener -> listener.afterCoreStop(helper));
    }
}
