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
package com.warxim.petep.extension.internal.tagger.intercept;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.internal.common.rulegroup.RuleGroup;
import com.warxim.petep.extension.internal.tagger.rule.TagRule;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.worker.Interceptor;

/**
 * Tag interceptor.
 * <p>Uses group of rules to tag PDUs.</p>
 */
public final class TagInterceptor extends Interceptor {
    private final RuleGroup<TagRule> group;

    /**
     * Constructs tag interceptor.
     * @param id Identifier of interceptor (index of the interceptor)
     * @param module Parent module of the interceptor
     * @param helper Helper for accessing running instance of PETEP core
     */
    public TagInterceptor(int id, TagInterceptorModule module, PetepHelper helper) {
        super(id, module, helper);

        this.group = module.getRuleGroup();
    }

    @Override
    public boolean prepare() {
        return true;
    }

    @Override
    public boolean intercept(PDU pdu) {
        if (pdu.hasTag("no_tagger") && !pdu.hasTag("tagger")) {
            return true;
        }

        for (var rule : group.getRules()) {
            if (rule.test(pdu)) {
                // Drop PDU if the tag is "drop".
                if (rule.getTag().equals("drop")) {
                    return false;
                }

                pdu.addTag(rule.getTag());
            }
        }

        return true;
    }

    @Override
    public void stop() {
        // No action needed.
    }
}
