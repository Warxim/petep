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
package com.warxim.petep.extension.internal.http.proxy;

import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.http.pdu.HttpRequestPdu;
import com.warxim.petep.extension.internal.http.pdu.HttpResponsePdu;
import com.warxim.petep.extension.internal.http.pdu.Opcode;
import com.warxim.petep.extension.internal.http.pdu.WebSocketPdu;
import com.warxim.petep.proxy.serizalization.ProxyDeserializer;
import com.warxim.petep.proxy.worker.Proxy;
import com.warxim.petep.util.BytesUtils;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Deserializer for:
 * <ul>
 *     <li>{@link WebSocketPdu}</li>
 *     <li>{@link HttpRequestPdu}</li>
 *     <li>{@link HttpResponsePdu}</li>
 * </ul>
 */
public class HttpProxyDeserializer implements ProxyDeserializer {
    @Override
    public Optional<PDU> deserializePdu(
            Proxy proxy,
            Connection connection,
            PduDestination destination,
            byte[] buffer,
            int size,
            Charset charset,
            Set<String> tags,
            Map<String, String> serializedMetadata) {
        // WebSockets
        if (serializedMetadata.containsKey("Fin")) {
            return Optional.of(
                    deserializeWebSocketPdu(proxy, connection, destination, buffer, size, charset, tags, serializedMetadata)
            );
        }

        // HTTP
        if (destination == PduDestination.SERVER) {
            return Optional.of(
                    deserializeHttpRequestPdu(proxy, connection, destination, buffer, size, charset, tags, serializedMetadata)
            );
        } else {
            return Optional.of(
                    deserializeHttpResponsePdu(proxy, connection, destination, buffer, size, charset, tags, serializedMetadata)
            );

        }
    }

    /**
     * Deserializes WebSocketPdu.
     */
    private WebSocketPdu deserializeWebSocketPdu(
            Proxy proxy,
            Connection connection,
            PduDestination destination,
            byte[] buffer,
            int size,
            Charset charset,
            Set<String> tags,
            Map<String, String> serializedMetadata) {
        var pdu = new WebSocketPdu(proxy, connection, destination, buffer, size, charset, tags);

        // FIN
        pdu.setFinal(serializedMetadata.get("Fin").equals("1"));

        // RSV
        var rsv = serializedMetadata.get("RSV").split(",");
        pdu.setRsv1(rsv[0].equals("1"));
        pdu.setRsv2(rsv[1].equals("1"));
        pdu.setRsv3(rsv[2].equals("1"));

        pdu.setOpcode(Opcode.valueOf(serializedMetadata.get("Opcode")));

        pdu.setMasked(serializedMetadata.get("Masked").equals("1"));

        if (pdu.isMasked()) {
            pdu.setMask(BytesUtils.hexStringToBytes(serializedMetadata.get("Mask")));
        }

        return pdu;
    }

    /**
     * Deserializes HttpRequestPdu.
     */
    private HttpRequestPdu deserializeHttpRequestPdu(
            Proxy proxy,
            Connection connection,
            PduDestination destination,
            byte[] buffer,
            int size,
            Charset charset,
            Set<String> tags,
            Map<String, String> serializedMetadata) {
        var request = new HttpRequestPdu(proxy, connection, destination, buffer, size, charset, tags);

        for (var item : serializedMetadata.entrySet()) {
            var key = item.getKey();

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
    }

    /**
     * Deserializes HttpResponsePdu.
     */
    private HttpResponsePdu deserializeHttpResponsePdu(
            Proxy proxy,
            Connection connection,
            PduDestination destination,
            byte[] buffer,
            int size,
            Charset charset,
            Set<String> tags,
            Map<String, String> serializedMetadata) {
        var response = new HttpResponsePdu(proxy, connection, destination, buffer, size, charset, tags);

        for (var item : serializedMetadata.entrySet()) {
            var key = item.getKey();

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
