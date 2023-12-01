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
package com.warxim.petep.core.connection;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.core.pdu.PduQueue;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.proxy.worker.Proxy;

/**
 * ConnectionBase is an abstract implementation of Connection, which uses two queues for outgoing data
 * and implements most of the required methods.
 */
@PetepAPI
public abstract class ConnectionBase implements Connection {
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
    protected ConnectionBase(String code, Proxy proxy) {
        this.code = code;
        this.proxy = proxy;
        this.queueC2S = new PduQueue();
        this.queueS2C = new PduQueue();
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public Proxy getProxy() {
        return proxy;
    }

    @Override
    public void send(PDU pdu) {
        if (pdu.getDestination() == PduDestination.SERVER) {
            queueC2S.add(pdu);
        } else {
            queueS2C.add(pdu);
        }
    }

    @Override
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
    @Override
    public abstract boolean start();

    /**
     * Stops connection.
     */
    @Override
    public abstract void stop();

    /**
     * Processes PDU in PETEP
     * <p>Puts PDU into internal PETEP processing, which consists of various configured interceptors.</p>
     * @param pdu PDU to be processed
     */
    protected void process(PDU pdu) {
        proxy.getHelper().processPdu(pdu);
    }
}
