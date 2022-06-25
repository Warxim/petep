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
package com.warxim.petep.extension.internal.tagger.factory.internal.destination;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.tagger.factory.TagSubrule;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleData;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleFactory;

/**
 * Tag subrule for checking whether the PDU goes in specified direction (to specified destination).
 */
public final class DestinationTagSubrule extends TagSubrule {
    /**
     * Constructs tag subrule for Destination subrule.
     * @param factory Factory that created this subrule
     * @param data Data for the subrule
     */
    public DestinationTagSubrule(TagSubruleFactory factory, TagSubruleData data) {
        super(factory, data);
    }

    @Override
    public boolean test(PDU pdu) {
        return pdu.getDestination() == ((DestinationData) data).getDestination();
    }

    @Override
    public String toString() {
        return ((DestinationData) data).getDestination() == PduDestination.CLIENT
                ? "To client"
                : "To server";
    }
}
