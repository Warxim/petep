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

import com.warxim.petep.extension.internal.scripter.rule.ScriptInterceptor;
import com.warxim.petep.helper.ExtensionHelper;

/**
 * Script helper for scripts to use various internal methods.
 */
public interface ScriptHelper {
    /**
     * Gets extension helper.
     * @return Extension helper
     */
    ExtensionHelper getExtensionHelper();

    /**
     * Gets logger.
     * @return Script logger for simple logging support in scripts
     */
    ScriptLogger getLogger();

    /**
     * Registers script interceptor.
     * @param interceptor Script interceptor to be registered
     */
    void registerInterceptor(ScriptInterceptor interceptor);

    /**
     * Loads script from given path and adds it to context.
     * @param path Path to script file
     */
    void require(String path);
}
