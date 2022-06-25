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
package com.warxim.petep.extension.internal.modifier.factory;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.PetepAPI;

/**
 * Modifier rule for processing modification of PDUs.
 */
@PetepAPI
public abstract class Modifier {
    protected final ModifierFactory factory;
    protected final ModifierData data;

    /**
     * Constructs modifier
     * @param factory Factory that created this modifier
     * @param data Data configuration for this modifier
     */
    protected Modifier(ModifierFactory factory, ModifierData data) {
        this.factory = factory;
        this.data = data;
    }

    /**
     * Processes PDU in the modifier.
     * @param pdu PDU to be processed
     * @return {@code false} if PDU should be dropped
     */
    public abstract boolean process(PDU pdu);

    /**
     * Gets modifier factory.
     * @return Modifier factory of this modifier
     */
    public ModifierFactory getFactory() {
        return factory;
    }

    /**
     * Gets modifier data configuration.
     * @return Modifier data of this modifier
     */
    public ModifierData getData() {
        return data;
    }

    /**
     * Creates deep copy of the Modifier.
     * @return Deep copy of the Modifier
     */
    public Modifier copy() {
        return factory.createModifier(data);
    }

    @Override
    public String toString() {
        return factory.getName();
    }
}
