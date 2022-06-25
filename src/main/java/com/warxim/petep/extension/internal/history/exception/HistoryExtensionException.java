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
package com.warxim.petep.extension.internal.history.exception;

/**
 * Exception thrown by History extension when something wrong occurs during initialization.
 */
public class HistoryExtensionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs history extension exception.
     * @param message Message describing what went wrong during initialization
     * @param cause Cause of the problem
     */
    public HistoryExtensionException(String message, Throwable cause) {
        super(message, cause);
    }
}
