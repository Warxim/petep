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
package com.warxim.petep.extension.receiver;

import com.warxim.petep.extension.PetepAPI;

/**
 * Receiver for cross extension data transfer.
 * <p>Receivers are identified by their code, so it has to be unique across the application.</p>
 * <p>Receivers can be registered through {@link com.warxim.petep.helper.ExtensionHelper}.</p>
 * <p><b>Note:</b> Receivers are kept using weak references, so they have to be stored somewhere as strong reference.</p>
 */
@PetepAPI
public interface Receiver {
    /**
     * Obtains name of the receiver.
     * @return Name for displaying in the GUI
     */
    String getName();

    /**
     * Obtains code of the receiver.
     * @return Code for identifying the receiver
     */
    String getCode();

    /**
     * Checks whether the receiver supports specified class (as input data).
     * @param clazz Class to check
     * @return {@code true} if the receiver supports given class
     */
    boolean supports(Class<?> clazz);

    /**
     * Receives data in receiver.
     * @param data Data to be received
     */
    void receive(Object data);
}
