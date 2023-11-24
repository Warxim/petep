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
package com.warxim.petep.core.connection;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.proxy.worker.Proxy;

/**
 * Connection in PETEP proxy has to handle both connection between client and proxy and between proxy and server.
 */
@PetepAPI
public interface Connection {
    /**
     * Obtains connection code
     * @return Connection code
     */
    String getCode();

    /**
     * Obtains connection parent proxy
     * @return Parent proxy
     */
    Proxy getProxy();

    /**
     * Sends PDU outside PETEP
     * @param pdu PDU to be sent
     */
    void send(PDU pdu);

    /**
     * Checks whether the PDU is supported by this connection.
     * @param pdu PDU to be checked
     * @return {@code true} if the connection supports the specified pdu
     */
    boolean supports(PDU pdu);

    /**
     * Converts connection to displayable string (e.g. in JavaFX components)
     * @return Displayable description of connection
     */
    String toString();

    /**
     * Starts connection.
     * <p>
     *     <b>Warning:</b> this method should return ASAP - it should be used to create threads and then return immediately.
     * </p>
     * @return {@code true} if the start was successful
     */
    boolean start();

    /**
     * Stops connection.
     */
    void stop();
}
