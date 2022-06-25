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
package com.warxim.petep.proxy.factory;

import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.gui.component.PduMetadataPane;
import com.warxim.petep.module.ModuleFactory;
import com.warxim.petep.proxy.module.ProxyModule;
import com.warxim.petep.proxy.serizalization.ProxyDeserializer;
import com.warxim.petep.proxy.serizalization.ProxySerializer;

import java.io.IOException;
import java.util.Optional;

/**
 * Proxy module factory.
 * <p>Used as base class for module factories, which produce proxy modules.</p>
 */
@PetepAPI
public abstract class ProxyModuleFactory extends ModuleFactory<ProxyModule> {
    /**
     * Constructs proxy module factory.
     * @param extension Extension that owns this factory
     */
    protected ProxyModuleFactory(Extension extension) {
        super(extension);
    }

    /**
     * Creates PDU metadata component for modules of this factory.
     * @return created PDU metadata pane (if it is supported by the module factory)
     * @throws IOException If the PDU metadata pane could not be created
     */
    public abstract Optional<PduMetadataPane> createPduMetadataPane() throws IOException;

    /**
     * Obtains proxy serializer.
     * @return Proxy serializer for serializing PDUs
     */
    public abstract ProxySerializer getSerializer();

    /**
     * Obtains proxy deserializer.
     * @return Proxy deserializer for deserializing PDUs
     */
    public abstract ProxyDeserializer getDeserializer();
}
