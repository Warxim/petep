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
package com.warxim.petep.extension.internal.tagger.factory;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.PetepAPI;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Tag subrule.
 */
@Getter
@AllArgsConstructor
@PetepAPI
public abstract class TagSubrule {
    protected final TagSubruleFactory factory;
    protected TagSubruleData data;

    /**
     * Checks whether the PDU matches the subrule.
     * @param pdu PDU to check
     * @return {@code true} if the PDU matches the subrule
     */
    public abstract boolean test(PDU pdu);

    /**
     * Creates deep copy of the Tag Subrule.
     * @return Deep copy of the Tag Subrule
     */
    public TagSubrule copy() {
        return factory.createSubrule(data);
    }

    @Override
    public String toString() {
        return factory.getName();
    }
}
