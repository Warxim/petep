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
package com.warxim.petep.core.pdu;

import com.warxim.petep.common.Constant;
import com.warxim.petep.extension.PetepAPI;
import lombok.Builder;
import lombok.Data;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Simple object representing serialized PDU.
 * <p>All PDU fields represented by data types, which can be serialized by PETEP.</p>
 * <p>Can be displayed by {@link com.warxim.petep.gui.control.SerializedPduView}.</p>
 * <p>Can be user for storing PDUs as part of configuration/store.</p>
 */
@Data
@Builder
@PetepAPI
public class SerializedPdu {
    /**
     * Proxy serialized as proxy code.
     */
    private String proxy;
    /**
     * Connection serialized as connection code.
     */
    private String connection;
    /**
     * Interceptor serialized as interceptor code.
     */
    private String interceptor;
    private PduDestination destination;
    private byte[] buffer;
    @Builder.Default
    private Charset charset = Constant.DEFAULT_CHARSET;
    @Builder.Default
    private Set<String> tags = new HashSet<>();
    /**
     * Map of serialized metadata (key = metadata name, value = metadata value)
     */
    @Builder.Default
    private Map<String, String> metadata = new HashMap<>();

    /**
     * Creates deep copy of the serialized PDU.
     * @return Deep copy of the serialized PDU
     */
    public SerializedPdu copy() {
        return SerializedPdu.builder()
                .proxy(proxy)
                .connection(connection)
                .interceptor(interceptor)
                .destination(destination)
                .buffer(buffer.clone())
                .charset(charset)
                .tags(new HashSet<>(tags))
                .metadata(new HashMap<>(metadata))
                .build();
    }
}
