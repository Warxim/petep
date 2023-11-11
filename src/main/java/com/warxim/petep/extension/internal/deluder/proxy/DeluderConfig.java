/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2023 Michal Válka
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
package com.warxim.petep.extension.internal.deluder.proxy;

import lombok.Builder;
import lombok.Value;

import java.nio.charset.Charset;

/**
 * Deluder configuration
 */
@Builder
@Value
public class DeluderConfig {
    /**
     * Host (IP address) of the PETEP
     */
    String petepHost;

    /**
     * Port of the PETEP
     */
    int petepPort;

    /**
     * Charset of data in PDUs
     */
    Charset charset;
}