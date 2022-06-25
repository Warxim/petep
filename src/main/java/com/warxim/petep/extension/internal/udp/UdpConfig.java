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
package com.warxim.petep.extension.internal.udp;

import lombok.Builder;
import lombok.Value;

import java.nio.charset.Charset;

/**
 * UDP configuration
 */
@Builder
@Value
public class UdpConfig {
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
}
