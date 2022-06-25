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
package com.warxim.petep.exception;

/**
 * Configuration exception for configuration errors
 * (configuration does not exist, cannot be accessed, invalid fields, ...)
 */
public final class ConfigurationException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs configuration exception.
     * @param message Description of the problem
     */
    public ConfigurationException(String message) {
        super(message);
    }

    /**
     * Constructs configuration exception.
     * @param message Description of the problem
     * @param cause Cause of the problem
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
