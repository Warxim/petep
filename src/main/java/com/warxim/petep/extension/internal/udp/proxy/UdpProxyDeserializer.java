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
package com.warxim.petep.extension.internal.udp.proxy;

import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.proxy.serizalization.ProxyDeserializer;
import com.warxim.petep.proxy.worker.Proxy;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Deserializer for {@link UdpPdu}.
 */
public class UdpProxyDeserializer implements ProxyDeserializer {
    @Override
    public Optional<PDU> deserializePdu(Proxy proxy,
                                        Connection connection,
                                        PduDestination destination,
                                        byte[] buffer,
                                        int size,
                                        Charset charset,
                                        Set<String> tags,
                                        Map<String, String> serializedMetadata) {
        return Optional.of(new UdpPdu(proxy, connection, destination, buffer, size, charset, tags));
    }
}
