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
package com.warxim.petep.extension.internal.modifier.rule;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.internal.common.rulegroup.Rule;
import com.warxim.petep.extension.internal.modifier.factory.Modifier;
import lombok.Getter;

/**
 * Modify rule.
 * <p>Wraps modifiers to make basic checks and then applies them to modify the processed PDUs.</p>
 */
@Getter
public final class ModifyRule extends Rule {
    /**
     * Required tag for running modification (empty if the modifier should be applied to all PDUs)
     */
    private final String tag;
    private final Modifier modifier;

    /**
     * Modify rule constructor.
     * @param name Name of the rule
     * @param description Description of the rule
     * @param enabled {@code true} if the rule should be used
     * @param tag Tag to expect on PDUs for modification
     * @param modifier Modifier for processing the modification
     */
    public ModifyRule(String name, String description, boolean enabled, String tag, Modifier modifier) {
        super(name, description, enabled);
        this.tag = tag.intern();
        this.modifier = modifier;
    }

    /**
     * Processes PDU by modifier.
     * @param pdu PDU to be processed
     * @return {@code false} if the PDU should be dropped ({@code true} if it should be sent to next modifier/interceptor)
     */
    public boolean process(PDU pdu) {
        if ((!enabled) || (!tag.isEmpty() && !pdu.hasTag(tag))) {
            return true;
        }

        return modifier.process(pdu);
    }

    /**
     * Creates deep copy of the Modify Rule.
     * @return Deep copy of the Modify Rule
     */
    public ModifyRule copy() {
        return new ModifyRule(name, description, enabled, tag, modifier.copy());
    }
}
