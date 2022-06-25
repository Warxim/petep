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
package com.warxim.petep.extension.internal.scripter.util;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.EnvironmentAccess;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotAccess;

/**
 * Utils for scripter extension
 */
public class ScripterUtils {
    private ScripterUtils() {}

    /**
     * Creates polyglot context.
     * @return New context
     */
    public static Context createContext() {
        var builder = createContextBuilder();
        return builder.build();
    }

    /**
     * Creates context builder.
     */
    private static Context.Builder createContextBuilder() {
        return Context.newBuilder()
                .allowExperimentalOptions(true)
                .allowHostClassLoading(true)
                .allowHostAccess(HostAccess.ALL)
                .allowAllAccess(true)
                .allowNativeAccess(true)
                .allowPolyglotAccess(PolyglotAccess.ALL)
                .allowEnvironmentAccess(EnvironmentAccess.INHERIT);
    }
}
