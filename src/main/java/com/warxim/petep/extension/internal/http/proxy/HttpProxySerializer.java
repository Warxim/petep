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

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.internal.http.pdu.HttpRequestPdu;
import com.warxim.petep.extension.internal.http.pdu.HttpResponsePdu;
import com.warxim.petep.extension.internal.http.pdu.WebSocketPdu;
import com.warxim.petep.proxy.serizalization.ProxySerializer;
import com.warxim.petep.util.BytesUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Serializer for:
 * <ul>
 *     <li>{@link WebSocketPdu}</li>
 *     <li>{@link HttpRequestPdu}</li>
 *     <li>{@link HttpResponsePdu}</li>
 * </ul>
 */
public class HttpProxySerializer implements ProxySerializer {
    @Override
    public Map<String, String> serializePduMetadata(PDU pdu) {
        if (pdu instanceof HttpRequestPdu) {
            return serializeHttpRequestPduMetadata((HttpRequestPdu) pdu);
        } else if (pdu instanceof HttpResponsePdu) {
            return serializeHttpResponsePduMetadata((HttpResponsePdu) pdu);
        } else if (pdu instanceof WebSocketPdu) {
            return serializeWebSocketPduMetadata((WebSocketPdu) pdu);
        }

        return Map.of();
    }

    /**
     * Serializes HttpRequestPdu metadata.
     */
    private Map<String, String> serializeHttpRequestPduMetadata(HttpRequestPdu requestPdu) {
        var metadata = new HashMap<String, String>();
        if (requestPdu.getVersion() != null) {
            metadata.put("Method", requestPdu.getMethod());
            metadata.put("Path", requestPdu.getPath());
            metadata.put("Version", requestPdu.getVersion());
        }

        for (var header : requestPdu.getHeaders().entrySet()) {
            metadata.put("H-" + header.getKey(), header.getValue());
        }
        return metadata;
    }

    /**
     * Serializes HttpResponsePdu metadata.
     */
    private Map<String, String> serializeHttpResponsePduMetadata(HttpResponsePdu responsePdu) {
        var metadata = new HashMap<String, String>();
        if (responsePdu.getVersion() != null) {
            metadata.put("Status-Code", String.valueOf(responsePdu.getStatusCode()));
            metadata.put("Status-Message", responsePdu.getStatusMessage());
            metadata.put("Version", responsePdu.getVersion());
        }

        for (var header : responsePdu.getHeaders().entrySet()) {
            metadata.put("H-" + header.getKey(), header.getValue());
        }
        return metadata;
    }

    /**
     * Serializes WebSocketPdu metadata.
     */
    private Map<String, String> serializeWebSocketPduMetadata(WebSocketPdu websocketPdu) {
        var metadata = new HashMap<String, String>();
        metadata.put("Fin", websocketPdu.isFinal() ? "1" : "0");
        metadata.put(
                "RSV",
                (websocketPdu.isRsv1() ? "1" : "0")
                        + ","
                        + (websocketPdu.isRsv2() ? "1" : "0")
                        + ","
                        + (websocketPdu.isRsv3() ? "1" : "0")
        );
        metadata.put("Opcode", websocketPdu.getOpcode().name());
        metadata.put("Masked", websocketPdu.isMasked() ? "1" : "0");

        if (websocketPdu.isMasked()) {
            metadata.put("Mask", BytesUtils.bytesToHexString(websocketPdu.getMask()));
        }
        return metadata;
    }
}
