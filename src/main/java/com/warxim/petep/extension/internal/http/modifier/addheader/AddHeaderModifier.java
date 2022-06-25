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
package com.warxim.petep.extension.internal.http.modifier.addheader;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.internal.http.pdu.HttpPdu;
import com.warxim.petep.extension.internal.modifier.factory.Modifier;
import com.warxim.petep.extension.internal.modifier.factory.ModifierData;
import com.warxim.petep.extension.internal.modifier.factory.ModifierFactory;

/**
 * Add Header modifier for automatically adding headers to HttpPdus
 */
public final class AddHeaderModifier extends Modifier {
    /**
     * Constructs modifier for Add Header rule
     * @param factory Factory that created this modifier
     * @param data Data configuration for this modifier
     */
    public AddHeaderModifier(ModifierFactory factory, ModifierData data) {
        super(factory, data);
    }

    @Override
    public boolean process(PDU pdu) {
        if (pdu instanceof HttpPdu) {
            ((HttpPdu) pdu).addHeader(((AddHeaderData) data).getHeader(), ((AddHeaderData) data).getValue());
        }

        return true;
    }
}
