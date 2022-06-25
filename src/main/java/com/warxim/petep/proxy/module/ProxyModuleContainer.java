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
package com.warxim.petep.proxy.module;

import com.warxim.petep.module.ModuleContainer;

import java.util.List;

/**
 * Proxy module container.
 * <p>
 *     Proxy modules are registered by the user in application settings.
 * </p>
 */
public final class ProxyModuleContainer extends ModuleContainer<ProxyModule> {
    /**
     * Creates proxy module container using specified module list.
     * @param modules List of modules to set into the container
     */
    public ProxyModuleContainer(List<ProxyModule> modules) {
        super(modules);
    }
}
