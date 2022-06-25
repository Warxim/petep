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

import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.proxy.worker.Proxy;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Proxy deserializer for deserializing PDUs.
 */
@PetepAPI
@FunctionalInterface
public interface ProxyDeserializer {
    /**
     * Deserializes PDU from its parts and serialized meta data.
     * @param proxy Proxy
     * @param connection Connection
     * @param destination Destination
     * @param buffer Data buffer
     * @param size Size of data in the buffer
     * @param charset Charset of data in the buffer
     * @param tags Set of tags
     * @param serializedMetadata Serialized metadata (key = metadata code, value = metadata value)
     * @return Deserialized PDU
     */
    Optional<PDU> deserializePdu(
            Proxy proxy,
            Connection connection,
            PduDestination destination,
            byte[] buffer,
            int size,
            Charset charset,
            Set<String> tags,
            Map<String, String> serializedMetadata);
}
