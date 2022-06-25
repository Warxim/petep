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
package com.warxim.petep.extension.internal.http.tagger.headercontains;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.internal.http.pdu.HttpPdu;
import com.warxim.petep.extension.internal.tagger.factory.TagSubrule;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleData;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleFactory;

/**
 * Subrule for checking that the PDU contains header with specified data.
 */
public final class HeaderContainsSubrule extends TagSubrule {
    /**
     * Constructs tag subrule for Header Contains rule.
     * @param factory Factory that created this subrule
     * @param data Data for the subrule
     */
    public HeaderContainsSubrule(TagSubruleFactory factory, TagSubruleData data) {
        super(factory, data);
    }

    @Override
    public boolean test(PDU pdu) {
        if (!(pdu instanceof HttpPdu)) {
            return false;
        }

        var httpPdu = (HttpPdu) pdu;
        var config = ((HeaderContainsData) data);
        if (config.getHeader().isBlank()) {
            return containsInAnyHeader(httpPdu, config.getValue());
        } else {
            return containsInHeader(httpPdu, config.getHeader(), config.getValue());
        }
    }

    @Override
    public String toString() {
        return "Header "
                + ((HeaderContainsData) data).getHeader()
                + " contains "
                + ((HeaderContainsData) data).getValue();
    }

    private boolean containsInHeader(HttpPdu pdu, String header, String what) {
        var value = pdu.getHeader(header);
        if (value == null) {
            return false;
        }
        return value.contains(what);
    }

    private boolean containsInAnyHeader(HttpPdu pdu, String what) {
        var headers = pdu.getHeaders();
        for (var entry : headers.entrySet()) {
            if (entry.getValue().contains(what)) {
                return true;
            }
        }
        return false;
    }
}
