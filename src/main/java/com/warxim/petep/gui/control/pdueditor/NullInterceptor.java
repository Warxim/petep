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
package com.warxim.petep.gui.control.pdueditor;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.interceptor.worker.Interceptor;

/**
 * Null interceptor representing "Send out of PETEP" choice.
 */
@PetepAPI
public class NullInterceptor extends Interceptor {
    /**
     * Constructs null interceptor.
     */
    public NullInterceptor() {
        super(0, null, null);
    }

    @Override
    public void stop() {
        // No action needed
    }

    @Override
    public boolean prepare() {
        return false;
    }

    @Override
    public boolean intercept(PDU pdu) {
        return false;
    }

    @Override
    public String toString() {
        return "Send out of PETEP";
    }
}
