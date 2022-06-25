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
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.core.pdu.PduQueue;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.proxy.worker.Proxy;

/**
 * Connection in PETEP proxy has to handle both connection between client and proxy and between
 * proxy and server. Contains two outgoing queues that contain PDUs that should be sent in a given
 * direction (C2S / S2C).
 */
@PetepAPI
public abstract class Connection {
    /**
     * Unique identifier of the connection.
     */
    protected final String code;

    /**
     * Parent proxy.
     */
    protected final Proxy proxy;

    /**
     * Outgoing queue in direction C2S (client -&gt; server).
     */
    protected final PduQueue queueC2S;

    /**
     * Outgoing queue in direction S2C (client &lt;- server).
     */
    protected final PduQueue queueS2C;

    /**
     * Constructs connection.
     * @param code Unique code of the connection
     * @param proxy Proxy to which the connection belongs
     */
    protected Connection(String code, Proxy proxy) {
        this.code = code;
        this.proxy = proxy;
        this.queueC2S = new PduQueue();
        this.queueS2C = new PduQueue();
    }

    public String getCode() {
        return code;
    }

    public Proxy getProxy() {
        return proxy;
    }

    /**
     * Sends PDU outside the PETEP.
     * <p>Adds the PDU into outgoing queue in direction depending on PDU.destination.</p>
     * @param pdu PDU to be sent
     */
    public final void send(PDU pdu) {
        if (pdu.getDestination() == PduDestination.SERVER) {
            queueC2S.add(pdu);
        } else {
            queueS2C.add(pdu);
        }
    }

    /**
     * Sends PDU outside the PETEP in direction C2S (client -&gt; server).
     * <p>Adds the PDU into outgoing queue in direction client -&gt; server.</p>
     * @param pdu PDU to be sent
     */
    public final void sendC2S(PDU pdu) {
        queueC2S.add(pdu);
    }

    /**
     * Sends PDU outside the PETEP in direction S2C (client &lt;- server).
     * <p>Adds the PDU into outgoing queue in direction server -&gt; client.</p>
     * @param pdu PDU to be sent
     */
    public final void sendS2C(PDU pdu) {
        queueS2C.add(pdu);
    }

    /**
     * Processes PDU in PETEP core.
     * <p>Puts PDU into internal PETEP processing, which consists of various cofnigured interceptors.</p>
     * @param pdu PDU to be processed
     */
    protected final void process(PDU pdu) {
        proxy.getHelper().processPdu(pdu);
    }

    /**
     * Checks whether the PDU is supported by this connection.
     * @param pdu PDU to be checked
     * @return {@code true} if the connection supports the specified pdu
     */
    public boolean supports(PDU pdu) {
        return proxy.supports(pdu);
    }

    /**
     * About connection.
     */
    @Override
    public String toString() {
        return "Connection '" + code + '\'';
    }

    /**
     * Starts connection.
     * <p>
     *     <b>Warning:</b> this method should return ASAP - it should be used to create threads and then return immediately.
     * </p>
     * @return {@code true} if the start was successful
     */
    public abstract boolean start();

    /**
     * Stops connection.
     */
    public abstract void stop();
}
