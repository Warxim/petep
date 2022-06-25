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
package com.warxim.petep.extension.internal.externalhttpproxy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * External HTTP proxy configuration.
 */
@Getter
@Setter
@AllArgsConstructor
public final class EHTTPPConfig {
    /**
     * IP of the internal HTTP server.
     */
    private String serverIp;
    /**
     * Port of the internal HTTP server.
     */
    private int serverPort;

    /**
     * IP of the HTTP proxy.
     */
    private String proxyIp;
    /**
     * Port of the HTTP proxy.
     */
    private int proxyPort;

    /**
     * Sets all fields.
     * @param serverIp Server IP address
     * @param serverPort Server port
     * @param proxyIp Proxy IP address
     * @param proxyPort Proxy port
     */
    public void set(String serverIp, int serverPort, String proxyIp, int proxyPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.proxyIp = proxyIp;
        this.proxyPort = proxyPort;
    }
}
