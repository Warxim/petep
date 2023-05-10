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
package com.warxim.petep.extension.internal.tcp.proxy;

import com.warxim.petep.extension.internal.tcp.TcpConfig;
import com.warxim.petep.extension.internal.tcp.proxy.plain.PlainTcpProxy;
import com.warxim.petep.extension.internal.tcp.proxy.starttls.StarttlsTcpProxy;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.persistence.Configurable;
import com.warxim.petep.proxy.factory.ProxyModuleFactory;
import com.warxim.petep.proxy.module.ProxyModule;
import com.warxim.petep.proxy.worker.Proxy;

/**
 * TCP proxy module.
 */
public final class TcpProxyModule extends ProxyModule implements Configurable<TcpConfig> {
    private TcpConfig config;

    /**
     * Constructs TCP proxy module.
     * @param factory Factory that created this module
     * @param code Code of this module
     * @param name Name of this module
     * @param description Description of this module
     * @param enabled {@code true} if the module should be used
     */
    public TcpProxyModule(
            ProxyModuleFactory factory,
            String code,
            String name,
            String description,
            boolean enabled) {
        super(factory, code, name, description, enabled);
    }

    @Override
    public Proxy createProxy(PetepHelper helper) {
        // For STARTTLS use special STARTTLS proxy that starts connections with STARTTLS support
        if (config.getServerSslConfig() != null && config.getServerSslConfig().isStartTls()) {
            return new StarttlsTcpProxy(this, helper, config);
        }

        // For plain-text and other SSL/TLS use plain TCP proxy
        return new PlainTcpProxy(this, helper, config);
    }

    @Override
    public TcpConfig saveConfig() {
        return config;
    }

    @Override
    public void loadConfig(TcpConfig config) {
        this.config = config;
    }
}
