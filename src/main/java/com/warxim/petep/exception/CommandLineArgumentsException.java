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
 * Command line arguments exception for invalid arguments.
 */
public final class CommandLineArgumentsException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs exception for command line arguments error.
     * @param message Message describing the problem
     */
    public CommandLineArgumentsException(String message) {
        super(message);
    }
}
