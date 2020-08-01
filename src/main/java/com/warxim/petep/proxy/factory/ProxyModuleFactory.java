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

import java.io.IOException;
import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.gui.component.PduMetadataPane;
import com.warxim.petep.module.ModuleFactory;
import com.warxim.petep.proxy.module.ProxyModule;
import com.warxim.petep.proxy.serizalization.ProxyDeserializer;
import com.warxim.petep.proxy.serizalization.ProxySerializer;

/** Proxy module factory. */
@PetepAPI
public abstract class ProxyModuleFactory extends ModuleFactory<ProxyModule> {
  public ProxyModuleFactory(Extension extension) {
    super(extension);
  }

  /** Creates PDU metadata component for modules of this factory. */
  public abstract PduMetadataPane createPduMetadataPane() throws IOException;

  /** Returns proxy serializer. */
  public abstract ProxySerializer getSerializer();

  /** Returns proxy deserializer. */
  public abstract ProxyDeserializer getDeserializer();
}
