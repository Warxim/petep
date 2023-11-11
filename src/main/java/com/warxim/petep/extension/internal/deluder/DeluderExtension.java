/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2023 Michal Válka
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
package com.warxim.petep.extension.internal.deluder;

import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.internal.deluder.proxy.DeluderProxyModuleFactory;
import com.warxim.petep.helper.ExtensionHelper;

import java.util.logging.Logger;

/**
 * Deluder extension for integration with Deluder for proxy unaware applications
 */
public class DeluderExtension extends Extension {
    /**
     * Deluder extension constructor.
     * @param path Path to the extension
     */
    public DeluderExtension(String path) {
        super(path);

        Logger.getGlobal().info("Deluder extension loaded.");
    }

    @Override
    public void init(ExtensionHelper helper) {
        helper.registerProxyModuleFactory(new DeluderProxyModuleFactory(this));

        Logger.getGlobal().info("Deluder extension registered.");
    }

    @Override
    public String getCode() {
        return "deluder";
    }

    @Override
    public String getName() {
        return "Deluder extension";
    }

    @Override
    public String getDescription() {
        return "Deluder extension adds support for Deluder proxy.";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }
}
