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

import com.warxim.petep.extension.internal.scripter.rule.ScriptInterceptorManager;
import com.warxim.petep.helper.ExtensionHelper;
import lombok.AllArgsConstructor;
import org.graalvm.polyglot.Context;

/**
 * Script helper factory for creating script helpers.
 */
@AllArgsConstructor
public class ScriptHelperFactory {
    private final ExtensionHelper extensionHelper;

    /**
     * Creates default script helper.
     * @param name Name of the script
     * @param language Scripting language of the script (js, ...)
     * @param contextPath Context path, in which the script should be executed
     * @param context Polyglot context
     * @param scriptInterceptorManager Script interceptor manager for registering interceptors
     * @return Generated script helper
     */
    public ScriptHelper createHelper(
            String name,
            String language,
            String contextPath,
            Context context,
            ScriptInterceptorManager scriptInterceptorManager) {
        return new DefaultScriptHelper(
                extensionHelper,
                new DefaultScriptLogger("[SCRIPT '" + name + "'] "),
                language,
                contextPath,
                context,
                scriptInterceptorManager
        );
    }
}
