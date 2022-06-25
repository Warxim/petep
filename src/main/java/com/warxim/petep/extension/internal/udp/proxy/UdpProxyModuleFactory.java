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
package com.warxim.petep.extension.internal.udp.proxy;

import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.internal.udp.UdpConfig;
import com.warxim.petep.extension.internal.udp.UdpConfigurator;
import com.warxim.petep.gui.component.ConfigPane;
import com.warxim.petep.gui.component.PduMetadataPane;
import com.warxim.petep.persistence.Configurator;
import com.warxim.petep.proxy.factory.ProxyModuleFactory;
import com.warxim.petep.proxy.module.ProxyModule;
import com.warxim.petep.proxy.serizalization.ProxyDeserializer;
import com.warxim.petep.proxy.serizalization.ProxySerializer;

import java.io.IOException;
import java.util.Optional;

/**
 * UDP proxy module factory
 */
public class UdpProxyModuleFactory extends ProxyModuleFactory implements Configurator<UdpConfig>  {
    private UdpProxyDeserializer deserializer;
    private UdpProxySerializer serializer;

    /**
     * Constructs UDP proxy module factory.
     * @param extension Extension that owns this factory
     */
    public UdpProxyModuleFactory(Extension extension) {
        super(extension);
        serializer = new UdpProxySerializer();
        deserializer = new UdpProxyDeserializer();
    }

    @Override
    public Optional<PduMetadataPane> createPduMetadataPane() {
        return Optional.empty();
    }

    @Override
    public ProxySerializer getSerializer() {
        return serializer;
    }

    @Override
    public ProxyDeserializer getDeserializer() {
        return deserializer;
    }

    @Override
    public String getName() {
        return "UDP";
    }

    @Override
    public String getCode() {
        return "udp";
    }

    @Override
    public ProxyModule createModule(String code, String name, String description, boolean enabled) {
        return new UdpProxyModule(this, code, name, description, enabled);
    }

    @Override
    public ConfigPane<UdpConfig> createConfigPane() throws IOException {
        return new UdpConfigurator();
    }
}
