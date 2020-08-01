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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.internal.http.gui.HttpMetadataPane;
import com.warxim.petep.extension.internal.http.pdu.HttpRequestPdu;
import com.warxim.petep.extension.internal.http.pdu.HttpResponsePdu;
import com.warxim.petep.extension.internal.http.pdu.Opcode;
import com.warxim.petep.extension.internal.http.pdu.WebSocketPdu;
import com.warxim.petep.extension.internal.tcp.TcpConfig;
import com.warxim.petep.extension.internal.tcp.TcpConfigurator;
import com.warxim.petep.gui.component.ConfigPane;
import com.warxim.petep.gui.component.PduMetadataPane;
import com.warxim.petep.persistence.Configurator;
import com.warxim.petep.proxy.factory.ProxyModuleFactory;
import com.warxim.petep.proxy.module.ProxyModule;
import com.warxim.petep.proxy.serizalization.ProxyDeserializer;
import com.warxim.petep.proxy.serizalization.ProxySerializer;
import com.warxim.petep.proxy.worker.Proxy;
import com.warxim.petep.util.BytesUtils;

/** HTTP proxy module. */
public final class HttpProxyModuleFactory extends ProxyModuleFactory
    implements Configurator<TcpConfig>, ProxySerializer, ProxyDeserializer {
  /** HTTP proxy module constructor. */
  public HttpProxyModuleFactory(Extension extension) {
    super(extension);
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
  public PduMetadataPane createPduMetadataPane() throws IOException {
    return new HttpMetadataPane();
  }

  @Override
  public Map<String, String> serializePduMetadata(PDU pdu) {
    Map<String, String> metadata = new HashMap<>();
    if (pdu instanceof HttpRequestPdu) {
      HttpRequestPdu request = (HttpRequestPdu) pdu;

      if (request.getVersion() != null) {
        metadata.put("Method", request.getMethod());
        metadata.put("Path", request.getPath());
        metadata.put("Version", request.getVersion());
      }

      for (var header : request.getHeaders().entrySet()) {
        metadata.put("H-" + header.getKey(), header.getValue());
      }

      return metadata;
    } else if (pdu instanceof HttpResponsePdu) {
      HttpResponsePdu response = (HttpResponsePdu) pdu;

      if (response.getVersion() != null) {
        metadata.put("Status-Code", String.valueOf(response.getStatusCode()));
        metadata.put("Status-Message", response.getStatusMessage());
        metadata.put("Version", response.getVersion());
      }

      for (var header : response.getHeaders().entrySet()) {
        metadata.put("H-" + header.getKey(), header.getValue());
      }

      return metadata;
    } else if (pdu instanceof WebSocketPdu) {
      WebSocketPdu websocketPdu = (WebSocketPdu) pdu;

      metadata.put("Fin", websocketPdu.isFinal() ? "1" : "0");
      metadata.put("RSV", (websocketPdu.isRsv1() ? "1" : "0") + ","
          + (websocketPdu.isRsv2() ? "1" : "0") + "," + (websocketPdu.isRsv3() ? "1" : "0"));
      metadata.put("Opcode", websocketPdu.getOpcode().name());
      metadata.put("Masked", websocketPdu.isMasked() ? "1" : "0");

      if (websocketPdu.isMasked()) {
        metadata.put("Mask", BytesUtils.bytesToString(websocketPdu.getMask()));
      }

      return metadata;
    }

    return null;
  }

  @Override
  public PDU deserializePdu(
      Proxy proxy,
      Connection connection,
      PduDestination destination,
      byte[] buffer,
      int size,
      Set<String> tags,
      Map<String, String> serializedMetadata) {
    // WebSockets
    if (serializedMetadata.containsKey("Fin")) {
      WebSocketPdu pdu = new WebSocketPdu(proxy, connection, destination, buffer, size, tags);

      // FIN
      pdu.setFinal(serializedMetadata.get("Fin").equals("1"));

      // RSV
      String[] rsv = serializedMetadata.get("RSV").split(",");
      pdu.setRsv1(rsv[0].equals("1"));
      pdu.setRsv2(rsv[1].equals("1"));
      pdu.setRsv3(rsv[2].equals("1"));

      pdu.setOpcode(Opcode.valueOf(serializedMetadata.get("Opcode")));

      pdu.setMasked(serializedMetadata.get("Masked").equals("1"));

      if (pdu.isMasked()) {
        pdu.setMask(BytesUtils.stringToBytes(serializedMetadata.get("Mask")));
      }

      return pdu;
    } else {
      // HTTP
      if (destination == PduDestination.SERVER) {
        HttpRequestPdu request =
            new HttpRequestPdu(proxy, connection, destination, buffer, size, tags);

        for (var item : serializedMetadata.entrySet()) {
          String key = item.getKey();

          if (key.startsWith("H-")) {
            request.addHeader(key.substring(2), item.getValue());
          } else if (key.equals("Method")) {
            request.setMethod(item.getValue());
          } else if (key.equals("Path")) {
            request.setPath(item.getValue());
          } else if (key.equals("Version")) {
            request.setVersion(item.getValue());
          }
        }

        return request;
      } else {
        HttpResponsePdu response =
            new HttpResponsePdu(proxy, connection, destination, buffer, size, tags);

        for (var item : serializedMetadata.entrySet()) {
          String key = item.getKey();

          if (key.startsWith("H-")) {
            response.addHeader(key.substring(2), item.getValue());
          } else if (key.equals("Status-Code")) {
            response.setStatusCode(Integer.parseInt(item.getValue()));
          } else if (key.equals("Status-Message")) {
            response.setStatusMessage(item.getValue());
          } else if (key.equals("Version")) {
            response.setVersion(item.getValue());
          }
        }

        return response;
      }
    }
  }

  @Override
  public ProxySerializer getSerializer() {
    return this;
  }

  @Override
  public ProxyDeserializer getDeserializer() {
    return this;
  }
}
