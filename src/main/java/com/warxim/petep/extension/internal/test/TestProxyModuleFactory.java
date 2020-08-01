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
package com.warxim.petep.extension.internal.test;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.Extension;
import com.warxim.petep.gui.component.ConfigPane;
import com.warxim.petep.gui.component.PduMetadataPane;
import com.warxim.petep.persistence.Configurator;
import com.warxim.petep.proxy.factory.ProxyModuleFactory;
import com.warxim.petep.proxy.module.ProxyModule;
import com.warxim.petep.proxy.serizalization.ProxyDeserializer;
import com.warxim.petep.proxy.serizalization.ProxySerializer;
import com.warxim.petep.proxy.worker.Proxy;

public final class TestProxyModuleFactory extends ProxyModuleFactory
    implements Configurator<TestProxyConfig>, ProxyDeserializer, ProxySerializer {
  public TestProxyModuleFactory(Extension extension) {
    super(extension);
  }

  @Override
  public String getName() {
    return "Test proxy module.";
  }

  @Override
  public String getCode() {
    return "test";
  }

  @Override
  public ProxyModule createModule(String code, String name, String description, boolean enabled) {
    return new TestProxyModule(this, code, name, description, enabled);
  }

  @Override
  public ConfigPane<TestProxyConfig> createConfigPane() throws IOException {
    return new TestProxyConfigurator();
  }

  @Override
  public PduMetadataPane createPduMetadataPane() throws IOException {
    return new TestPduMetadataPane();
  }

  @Override
  public ProxySerializer getSerializer() {
    return this;
  }

  @Override
  public ProxyDeserializer getDeserializer() {
    return this;
  }

  @Override
  public Map<String, String> serializePduMetadata(PDU pdu) {
    return Map.of("Test", ((TestPdu) pdu).getTest());
  }

  @Override
  public PDU deserializePdu(
      Proxy proxy,
      Connection connection,
      PduDestination destination,
      byte[] buffer,
      int size,
      Set<String> tags,
      Map<String, String> serializedMetaData) {
    return new TestPdu(proxy, connection, destination, buffer, size, tags,
        serializedMetaData.get("Test"));
  }
}
