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
package com.warxim.petep.extension.internal.http.modifier.replaceheader;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.internal.http.pdu.HttpPdu;
import com.warxim.petep.extension.internal.modifier.factory.Modifier;
import com.warxim.petep.extension.internal.modifier.factory.ModifierData;
import com.warxim.petep.extension.internal.modifier.factory.ModifierFactory;

/**
 * Replace Header modifier for automatically replacing header values in HttpPdus
 */
public final class ReplaceHeaderModifier extends Modifier {
    /**
     * Constructs modifier for Replace Header rule
     * @param factory Factory that created this modifier
     * @param data Data configuration for this modifier
     */
    public ReplaceHeaderModifier(ModifierFactory factory, ModifierData data) {
        super(factory, data);
    }

    @Override
    public boolean process(PDU pdu) {
        if (!(pdu instanceof HttpPdu)) {
            return true;
        }

        var httpPdu = ((HttpPdu) pdu);
        var replaceData = ((ReplaceHeaderData) data);

        if (replaceData.getHeader().isBlank()) {
            replaceInAllHeaders(httpPdu, replaceData.getWhat(), replaceData.getWith());
        } else {
            replaceInHeader(httpPdu, replaceData.getHeader(), replaceData.getWhat(), replaceData.getWith());
        }

        return true;
    }

    private void replaceInHeader(HttpPdu pdu, String header, String what, String with) {
        var value = pdu.getHeader(header);
        if (value == null) {
            return;
        }

        var newValue = value.replace(what, with);
        pdu.addHeader(header, newValue);
    }

    private void replaceInAllHeaders(HttpPdu pdu, String what, String with) {
        var headers = pdu.getHeaders();
        for (var entry : headers.entrySet()) {
            entry.setValue(entry.getValue().replace(what, with));
        }
    }
}
