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
package com.warxim.petep.extension.internal.http.tagger.ishttp;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.internal.http.pdu.HttpPdu;
import com.warxim.petep.extension.internal.tagger.factory.TagSubrule;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleData;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleFactory;

/**
 * Subrule for that the PDU is {@link HttpPdu}.
 */
public final class IsHttpSubrule extends TagSubrule {
    /**
     * Constructs tag subrule for Is HTTP rule.
     * @param factory Factory that created this subrule
     * @param data Data for the subrule
     */
    public IsHttpSubrule(TagSubruleFactory factory, TagSubruleData data) {
        super(factory, data);
    }

    @Override
    public boolean test(PDU pdu) {
        return pdu instanceof HttpPdu;
    }

    @Override
    public String toString() {
        return "Is HTTP";
    }
}
