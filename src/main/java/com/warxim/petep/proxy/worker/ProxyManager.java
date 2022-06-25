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
package com.warxim.petep.proxy.worker;

import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.module.ModuleWorkerManager;
import com.warxim.petep.proxy.module.ProxyModuleContainer;

/**
 * Proxy manager.
 * <p>Manages active proxies for running core.</p>
 */
public final class ProxyManager extends ModuleWorkerManager<Proxy> {
    /**
     * Constructs proxy manager.
     * <p>Creates list and map of proxy workers by generating workers using enabled modules.</p>
     * @param container Container containing modules for generation
     * @param helper PETEP helper for currently running core
     */
    public ProxyManager(
            PetepHelper helper,
            ProxyModuleContainer container) {
        super(container, ((module, index) -> module.createProxy(helper)));
    }
}
