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
package com.warxim.petep.extension.internal.tcp;

import lombok.Builder;
import lombok.Value;

import java.nio.charset.Charset;

/**
 * TCP configuration.
 */
@Builder
@Value
public class TcpConfig {
    /**
     * IP address of the PETEP proxy.
     */
    String proxyIP;
    /**
     * IP address of the target server.
     */
    String targetIP;

    /**
     * Port of the PETEP proxy.
     */
    int proxyPort;
    /**
     * Port of the target server.
     */
    int targetPort;

    /**
     * Size of buffer for PDUs in bytes
     */
    int bufferSize;

    /**
     * Charset of data in PDUs
     */
    Charset charset;

    /**
     * After how many milliseconds to close the connection, when one of connection sockets closes.
     */
    int connectionCloseDelay;

    /**
     * Configuration of SSL/TLS for server on the proxy (Client &lt;-&gt; Proxy)
     */
    SslConfig serverSslConfig;

    /**
     * Configuration of SSL/TLS for client of the proxy (Proxy &lt;-&gt; Server)
     */
    SslConfig clientSslConfig;
}
