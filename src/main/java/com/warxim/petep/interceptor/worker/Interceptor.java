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
package com.warxim.petep.interceptor.worker;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.module.InterceptorModule;
import com.warxim.petep.module.ModuleWorker;
import lombok.Getter;

/**
 * Interceptor base class.
 * <p>Interceptors are made for intercepting PDUs during their processing in PETEP.</p>
 * <p>Interceptors can change data, modify metadata, drop PDUs and so on.</p>
 */
@Getter
@PetepAPI
public abstract class Interceptor extends ModuleWorker<InterceptorModule> {
    /**
     * ID of interceptor (interceptor index in interceptors list).
     */
    protected final int id;

    /**
     * Constructs interceptor.
     * @param id Identifier of interceptor (index of the interceptor)
     * @param module Parent module of the interceptor
     * @param helper Helper for accessing running instance of PETEP core
     */
    protected Interceptor(int id, InterceptorModule module, PetepHelper helper) {
        super(module, helper);
        this.id = id;
    }

    /**
     * Prepares instance for intercepting.
     * @return  {@code true} if the interceptor has been successfully prepared;<br>
     *          {@code false} if the interceptor has failed preparation (this will abort start of PETEP core)
     */
    public abstract boolean prepare();

    /**
     * Intercepts PDUs.
     * @param pdu PDU to be intercepted
     * @return  {@code true} if the PDU should be send to next interceptor;<br>
     *          {@code false} if the PDU should be dropped
     */
    public abstract boolean intercept(PDU pdu);

    /**
     * Stops intercepting.
     */
    public abstract void stop();
}
