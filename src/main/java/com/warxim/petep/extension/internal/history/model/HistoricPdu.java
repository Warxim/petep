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
package com.warxim.petep.extension.internal.history.model;

import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.PetepAPI;
import lombok.*;

import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * Historic PDU model
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@PetepAPI
public class HistoricPdu {
    /**
     * Identifier of the PDU in the database
     */
    private Long id;
    /**
     * Originating proxy
     */
    private HistoricProxy proxy;
    /**
     * Originating connection
     */
    private HistoricConnection connection;
    /**
     * Originating interceptor (where the PDU has been recorded)
     */
    private HistoricInterceptor interceptor;
    /**
     * Destination of the PDU
     */
    private PduDestination destination;
    /**
     * Set of PDU tags
     */
    private Set<String> tags;
    /**
     * Size of PDU data
     */
    private int size;
    /**
     * Time when the PDU has been recorded
     */
    private Instant time;
    /**
     * Charset of the data
     */
    private Charset charset;
    /**
     * Data
     */
    private byte[] data;
    /**
     * Serialized metadata (key = metadata name, value = metadata value)
     */
    private Map<String, String> metadata;
}
