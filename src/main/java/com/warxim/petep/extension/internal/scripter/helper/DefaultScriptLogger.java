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

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

import java.util.logging.Logger;

/**
 * Default implementation of script logger.
 */
@Log
@AllArgsConstructor
public class DefaultScriptLogger implements ScriptLogger {
    private static final Logger LOGGER = Logger.getGlobal();
    private final String prefix;

    @Override
    public void info(String message) {
        LOGGER.info(() -> prefix + message);
    }

    @Override
    public void warning(String message) {
        LOGGER.warning(() -> prefix + message);
    }

    @Override
    public void error(String message) {
        LOGGER.severe(() -> prefix + message);
    }
}
