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
package com.warxim.petep.extension.internal.scripter.helper;

/**
 * Script logger for simple logging inside scripts.
 */
public interface ScriptLogger {
    /**
     * Logs message with INFO level.
     * @param message Message to be logged
     */
    void info(String message);

    /**
     * Logs message with WARNING level.
     * @param message Message to be logged
     */
    void warning(String message);

    /**
     * Logs message with ERROR level.
     * @param message Message to be logged
     */
    void error(String message);
}
