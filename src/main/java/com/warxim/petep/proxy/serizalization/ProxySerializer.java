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
package com.warxim.petep.proxy.serizalization;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.PetepAPI;

import java.util.Map;

/**
 * Proxy serializer for serializing PDU metadata to map.
 */
@PetepAPI
@FunctionalInterface
public interface ProxySerializer {
    /**
     * Serializes PDU meta data to map of strings.
     * @param pdu PDU with metadata to be deserialized
     * @return Metadata map (key = metadata code, value = metadata value)
     */
    Map<String, String> serializePduMetadata(PDU pdu);
}
