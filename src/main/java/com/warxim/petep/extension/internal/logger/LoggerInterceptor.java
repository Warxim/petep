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
package com.warxim.petep.extension.internal.logger;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.worker.Interceptor;

/**
 * Logger interceptor.
 */
public final class LoggerInterceptor extends Interceptor {
    private LoggerWorker worker;

    /**
     * Logger interceptor constructor.
     * @param id Identifier of interceptor (index of the interceptor)
     * @param module Parent module of the interceptor
     * @param helper Helper for accessing running instance of PETEP core
     */
    public LoggerInterceptor(int id, LoggerInterceptorModule module, PetepHelper helper) {
        super(id, module, helper);
    }

    @Override
    public boolean intercept(PDU pdu) {
        if (!pdu.hasTag("no_log") || pdu.hasTag("log")) {
            worker.log(pdu.copy());
        }

        return true;
    }

    @Override
    public boolean prepare() {
        return true;
    }

    @Override
    public void stop() {
        // No action needed.
    }

    public void setWorker(LoggerWorker worker) {
        this.worker = worker;
    }
}
