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
import com.warxim.petep.extension.internal.scripter.rule.ScriptInterceptorManager;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.util.FileUtils;
import lombok.AllArgsConstructor;
import org.graalvm.polyglot.Context;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of script helper.
 */
@AllArgsConstructor
public class DefaultScriptHelper implements ScriptHelper {
    private final ExtensionHelper extensionHelper;
    private final ScriptLogger scriptLogger;
    private final String language;
    private final String contextPath;
    private final Context context;
    private final ScriptInterceptorManager scriptInterceptorManager;

    @Override
    public ExtensionHelper getExtensionHelper() {
        return extensionHelper;
    }

    @Override
    public ScriptLogger getLogger() {
        return scriptLogger;
    }

    @Override
    public void registerInterceptor(ScriptInterceptor interceptor) {
        scriptInterceptorManager.register(interceptor);
    }

    @Override
    public void require(String path) {
        try {
            context.eval(
                    language,
                    Files.readString(Paths.get(
                            FileUtils.getFileAbsolutePath(contextPath, path)
                    ))
            );
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, String.format("Could not load required script '%s'!", path), e);
        }
    }
}
