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
package com.warxim.petep.extension.internal.http.pdu;

import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.tcp.proxy.base.TcpPdu;
import com.warxim.petep.proxy.worker.Proxy;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * HTTP PDU base for both request and response PDUs.
 */
@Getter
@Setter
public abstract class HttpPdu extends TcpPdu {
    protected String version;
    protected Map<String, String> headers;

    protected HttpPdu(Proxy proxy, Connection connection, PduDestination destination, byte[] buffer, int size, Charset charset) {
        super(proxy, connection, destination, buffer, size, charset);
        headers = new HashMap<>();
    }

    protected HttpPdu(Proxy proxy,
                   Connection connection,
                   PduDestination destination,
                   byte[] buffer,
                   int size,
                   Charset charset,
                   Set<String> tags) {
        super(proxy, connection, destination, buffer, size, charset, tags);
        headers = new HashMap<>();
    }

    /**
     * Adds headers to existing headers.
     * @param headers Headers to add
     */
    public void addHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    /**
     * Replaces existing headers with new headers.
     * @param headers Headers to set
     */
    public void setHeaders(Map<String, String> headers) {
        this.headers = new HashMap<>(headers);
    }

    /**
     * Adds header to existing headers.
     * @param name Header name to add
     * @param value Header value to add
     */
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    /**
     * Removes header from headers.
     * @param name Name of the header to remove
     */
    public void removeHeader(String name) {
        headers.remove(name);
    }

    /**
     * Obtains a header value from headers.
     * @param name Name of the header to get
     * @return Header value or null if it does not exist
     */
    public String getHeader(String name) {
        return headers.get(name);
    }
}
