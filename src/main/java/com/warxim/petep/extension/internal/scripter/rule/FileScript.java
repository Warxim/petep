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
package com.warxim.petep.extension.internal.scripter.rule;

import com.warxim.petep.extension.internal.scripter.helper.ScriptHelperFactory;
import com.warxim.petep.util.FileUtils;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * File based script.
 * <p>File script runs in context of the directory, in which it is created.</p>
 */
@Getter
public class FileScript extends Script {
    private final String path;

    /**
     * Constructs script.
     * @param name Name of the script
     * @param description Description of the script
     * @param enabled {@code true} if the script should be used
     * @param language Language of the script
     * @param factory Factory for creation of script helpers
     * @param path Path to the script
     */
    public FileScript(String name, String description, boolean enabled, String language, ScriptHelperFactory factory, String path) {
        super(name, description, enabled, language);
        this.path = path;
        if (enabled) {
            initContext(factory);
        }
    }

    /**
     * Initialized context (creates and binds helper, ...).
     */
    private void initContext(ScriptHelperFactory factory) {
        try {
            var contextPath = Paths.get(FileUtils.getProjectFileAbsolutePath(path)).getParent().toString();
            var script = Files.readString(Paths.get(FileUtils.getProjectFileAbsolutePath(path)));
            initContext(factory, script, contextPath);
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, String.format("Could not load file script '%s'!", path), e);
        }
    }

    @Override
    public ScriptType getType() {
        return ScriptType.FILE;
    }

    @Override
    public Script copy(ScriptHelperFactory factory) {
        return new FileScript(name, description, enabled, language, factory, path);
    }
}
