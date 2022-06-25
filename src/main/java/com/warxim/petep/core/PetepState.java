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
package com.warxim.petep.core;

import com.warxim.petep.extension.PetepAPI;

/**
 * State of PETEP core.
 */
@PetepAPI
public enum PetepState {
    /**
     * PETEP core and its components are preparing to start...
     */
    PREPARING,
    /**
     * PETEP core and its components are prepared for start...
     */
    PREPARED,
    /**
     * PETEP core is starting...
     */
    STARTING,
    /**
     * PETEP core is started and running...
     */
    STARTED,
    /**
     * PETEP core is stopping...
     */
    STOPPING,
    /**
     * PETEP core is stopped and inactive...
     */
    STOPPED
}
