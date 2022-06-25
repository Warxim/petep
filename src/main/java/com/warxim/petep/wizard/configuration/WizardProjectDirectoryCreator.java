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
package com.warxim.petep.wizard.configuration;

import com.warxim.petep.common.Constant;
import com.warxim.petep.util.FileUtils;

import java.io.IOException;

/**
 * Static class for project directory creation.
 */
public final class WizardProjectDirectoryCreator {
    private WizardProjectDirectoryCreator() {
    }

    /**
     * Creates project directory from template (./project_template).
     * @param path Path to project directory
     * @throws IOException If the creation failed
     */
    public static void create(String path) throws IOException {
        FileUtils.copyDirectory(
                FileUtils.getApplicationFileAbsolutePath(Constant.PROJECT_TEMPLATE_DIRECTORY),
                path
        );
    }
}
