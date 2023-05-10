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
package com.warxim.petep.extension.internal.tcp.proxy.starttls;

/**
 * State of communication between the proxy and the target server
 * <p>Hacky way to avoid problems with inputStream reading during transition between plaintext and SSL/TLS</p>
 */
public enum StarttlsP2SState {
    /**
     * Plain-text communication is active, reading from server uses busy wait
     */
    PLAIN_TEXT_MODE,
    /**
     * We are starting to move to encrypted communication, but we have to wait until read from server notifies,
     * that we can continue, by moving to the next step
     */
    TRANSITION_STEP_1,
    /**
     * Read from server worker is waiting for the transition to SSL/TLS
     */
    TRANSITION_STEP_2,
    /**
     * We have successfully transitioned to encrypted mode, everything can continue normally
     */
    ENCRYPTION_MODE
}