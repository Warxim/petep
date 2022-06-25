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
package com.warxim.petep.extension.internal.scripter.intercept;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.internal.common.rulegroup.RuleGroup;
import com.warxim.petep.extension.internal.scripter.rule.Script;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.worker.Interceptor;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Script interceptor for applying group of scripts on PDUs.
 */
public class ScripterInterceptor extends Interceptor {
    private final RuleGroup<Script> group;

    /**
     * Constructs scripter interceptor.
     * @param id Identifier of interceptor (index of the interceptor)
     * @param module Parent module of the interceptor
     * @param helper Helper for accessing running instance of PETEP core
     */
    public ScripterInterceptor(int id, ScripterInterceptorModule module, PetepHelper helper) {
        super(id, module, helper);
        group = module.getRuleGroup();
    }

    @Override
    public boolean prepare() {
        return true;
    }

    @Override
    public boolean intercept(PDU pdu) {
        if (pdu.hasTag("no_scripter") && !pdu.hasTag("scripter")) {
            return true;
        }

        for (var script : group.getRules()) {
            try {
                synchronized(script) {
                    if (script.isEnabled() && !script.getScriptInterceptorManager().intercept(pdu, helper)) {
                        return false;
                    }
                }
            } catch (RuntimeException e) {
                Logger.getGlobal().log(
                        Level.SEVERE,
                        String.format("Exception occurred during execution of script '%s'!", script.getName()),
                        e
                );
            }
        }
        return true;
    }

    @Override
    public void stop() {
        // No action needed
    }
}
