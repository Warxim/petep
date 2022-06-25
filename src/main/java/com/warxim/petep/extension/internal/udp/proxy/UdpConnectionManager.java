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

import com.warxim.petep.core.connection.StringBasedConnectionManager;
import com.warxim.petep.helper.PetepHelper;

import java.net.InetAddress;

/**
 * UDP connection manager based on {@link StringBasedConnectionManager}.
 * <p>UDP connection code contains information about address and port.</p>
 */
public class UdpConnectionManager extends StringBasedConnectionManager {
    /**
     * Constructs connection manager based on string codes.
     * @param helper PETEP helper for currently running core
     */
    public UdpConnectionManager(PetepHelper helper) {
        super(helper);
    }

    /**
     * Generates connection code for identifying UDP connections.
     * <p>Code has the following format: {clientAddress}:{clientPort}</p>
     * @param clientAddress Client address to be used for generation
     * @param clientPort Client port to be used for generation
     * @return Generated connection code
     */
    public String generateCode(InetAddress clientAddress, int clientPort) {
        return clientAddress.getHostAddress() + ':' + clientPort;
    }
}
