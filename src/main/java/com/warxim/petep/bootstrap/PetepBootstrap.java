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
package com.warxim.petep.bootstrap;

import com.warxim.petep.Bundle;
import com.warxim.petep.exception.ConfigurationException;
import com.warxim.petep.util.FileUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Super class for bootstraps
 */
public abstract class PetepBootstrap {
    protected CommandLineArguments arguments;

    /**
     * Constructs PetepBoostrap.
     * @param arguments Arguments for starting the application
     */
    protected PetepBootstrap(CommandLineArguments arguments) {
        this.arguments = arguments;
    }

    /**
     * Starts PETEP project (loads assets etc.)
     * @throws BootstrapException Exception signalizing that boostrap failed
     */
    public void start() throws BootstrapException {
        try {
            // Initialize utils.
            FileUtils.setProjectDirectory(
                    FileUtils.getWorkingDirectoryFileAbsolutePath(arguments.getProjectPath()));

            // Load PETEP bundle.
            Bundle.getInstance().load(arguments);

            Logger.getGlobal().log(
                    Level.INFO,
                    () -> "Loaded project " + Bundle.getInstance().getProject().getName() + " (" + arguments.getProjectPath() + ")!");
        } catch (ConfigurationException e) {
            throw new BootstrapException("Project configuration exception occured!", e);
        } catch (RuntimeException e) {
            throw new BootstrapException("Unexpected exception occurred!", e);
        }
    }
}
