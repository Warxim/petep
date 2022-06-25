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
package com.warxim.petep.extension.internal.scripter.rule;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.helper.PetepHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * Interceptor manager for managing multiple script interceptors inside single script.
 */
public class ScriptInterceptorManager implements ScriptInterceptor {
    private final List<ScriptInterceptor> interceptors = new LinkedList<>();

    /**
     * Registers script interceptor.
     * @param scriptInterceptor Script interceptor to be registered
     */
    public void register(ScriptInterceptor scriptInterceptor) {
        interceptors.add(scriptInterceptor);
    }

    @Override
    public boolean intercept(PDU pdu, PetepHelper helper) {
        for (var interceptor : interceptors) {
            if (!interceptor.intercept(pdu, helper)) {
                return false;
            }
        }
        return true;
    }
}
