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
package com.warxim.petep.extension.internal.http.proxy;

import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.internal.http.gui.HttpMetadataPane;
import com.warxim.petep.extension.internal.tcp.TcpConfig;
import com.warxim.petep.extension.internal.tcp.TcpConfigurator;
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
 * HTTP proxy module.
 */
public final class HttpProxyModuleFactory extends ProxyModuleFactory implements Configurator<TcpConfig> {
    private final HttpProxySerializer serializer;
    private final HttpProxyDeserializer deserializer;

    /**
     * Constructs HTTP proxy module factory.
     * @param extension Extension that owns this factory
     */
    public HttpProxyModuleFactory(Extension extension) {
        super(extension);
        serializer = new HttpProxySerializer();
        deserializer = new HttpProxyDeserializer();
    }

    @Override
    public String getName() {
        return "HTTP";
    }

    @Override
    public String getCode() {
        return "http";
    }

    @Override
    public ProxyModule createModule(String code, String name, String description, boolean enabled) {
        return new HttpProxyModule(this, code, name, description, enabled);
    }

    @Override
    public ConfigPane<TcpConfig> createConfigPane() throws IOException {
        return new TcpConfigurator();
    }

    @Override
    public Optional<PduMetadataPane> createPduMetadataPane() throws IOException {
        return Optional.of(new HttpMetadataPane());
    }

    @Override
    public ProxySerializer getSerializer() {
        return serializer;
    }

    @Override
    public ProxyDeserializer getDeserializer() {
        return deserializer;
    }
}
