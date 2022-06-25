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

import java.time.Instant;
import java.util.Set;

/**
 * Historic pdu view model
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@PetepAPI
public class HistoricPduView {
    /**
     * Identifier of the PDU
     */
    private long id;
    /**
     * Identifier of the originating proxy in the database
     */
    private long proxyId;
    /**
     * Name of the originating proxy
     */
    private String proxyName;
    /**
     * Identifier of the originating connection in the database
     */
    private long connectionId;
    /**
     * Name of the originating connection
     */
    private String connectionName;
    /**
     * Identifier of the originating connection in the database (where the PDU has been recorded)
     */
    private long interceptorId;
    /**
     * Name of the originating interceptor
     */
    private String interceptorName;
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
}
