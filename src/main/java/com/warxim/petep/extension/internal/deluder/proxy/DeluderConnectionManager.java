/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2023 Michal Válka
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
package com.warxim.petep.extension.internal.deluder.proxy;

import com.warxim.petep.core.connection.IntegerBasedConnectionManager;
import com.warxim.petep.helper.PetepHelper;

/**
 * Deluder connection manager
 */
public class DeluderConnectionManager extends IntegerBasedConnectionManager {
    /**
     * Constructs connection manager based on integer codes.
     * @param helper PETEP helper for currently running core
     */
    public DeluderConnectionManager(PetepHelper helper) {
        super(helper);
    }
}
