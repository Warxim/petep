/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2021 Michal Válka
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
package com.warxim.petep.extension.internal.udp;

import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.internal.udp.proxy.UdpProxyModuleFactory;
import com.warxim.petep.helper.ExtensionHelper;

import java.util.logging.Logger;

/**
 * UDP extension.
 * <p>Adds support for basic UDP proxies.</p>
 */
public class UdpExtension extends Extension {
    /**
     * Constructs UDP extension.
     * @param path Path to the extension
     */
    public UdpExtension(String path) {
        super(path);

        Logger.getGlobal().info("UDP extension loaded.");
    }

    @Override
    public void init(ExtensionHelper helper) {
        helper.registerProxyModuleFactory(new UdpProxyModuleFactory(this));

        Logger.getGlobal().info("TCP extension registered.");
    }

    @Override
    public String getCode() {
        return "udp";
    }

    @Override
    public String getName() {
        return "UDP extension";
    }

    @Override
    public String getDescription() {
        return "UDP extension adds UDP proxy to PETEP.";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }
}
