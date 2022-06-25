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
package com.warxim.petep.proxy.worker;

import com.warxim.petep.core.connection.ConnectionManager;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.module.ModuleWorker;
import com.warxim.petep.proxy.module.ProxyModule;

/**
 * Proxy base class.
 */
@PetepAPI
public abstract class Proxy extends ModuleWorker<ProxyModule> {
    /**
     * Constructs proxy
     * @param module Parent module of the worker
     * @param helper Helper for accessing running instance of PETEP core
     */
    protected Proxy(ProxyModule module, PetepHelper helper) {
        super(module, helper);
    }

    /**
     * Prepares proxy to start.
     * @return  {@code true} if the proxy has been successfully prepared;<br>
     *          {@code false} if the proxy has failed preparation (this will abort start of PETEP core)
     */
    public abstract boolean prepare();

    /**
     * Starts proxy.
     * @return  {@code true} if the proxy has been successfully started;<br>
     *          {@code false} if the proxy has failed to start (this will abort start of PETEP core)
     */
    public abstract boolean start();

    /**
     * Stops proxy.
     */
    public abstract void stop();

    /**
     * Checks whether the proxy supports the specified pdu.
     * @param pdu PDU to be checked
     * @return {@code true} if the PDU is supported by this proxy
     */
    public abstract boolean supports(PDU pdu);

    /**
     * Obtains proxy connection manager.
     * @return Current connection manager of this proxy
     */
    public abstract ConnectionManager getConnectionManager();
}
