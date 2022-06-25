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
package com.warxim.petep.extension.internal.scripter.rule;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.helper.PetepHelper;

/**
 * Script interceptor for intercepting PDUs inside of scripts.
 */
public interface ScriptInterceptor {
    /**
     * Intercepts PDU in script.
     * @param pdu PDU to be processed
     * @param helper Helper for currently running PETEP core
     * @return  {@code false} if the PDU should be dropped;<br>
     *          ({@code true} if it should be sent to next modifier/interceptor
     */
    boolean intercept(PDU pdu, PetepHelper helper);
}
