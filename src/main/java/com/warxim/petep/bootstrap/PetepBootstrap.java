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

import java.util.logging.Level;
import java.util.logging.Logger;
import com.warxim.petep.Bundle;
import com.warxim.petep.exception.ConfigurationException;
import com.warxim.petep.util.FileUtils;

/** Super class for bootstraps */
public abstract class PetepBootstrap {
  protected CommandLineArguments arguments;

  /**
   * PetepBootstrap constructor.
   *
   * @param arguments arguments
   */
  public PetepBootstrap(CommandLineArguments arguments) {
    this.arguments = arguments;
  }

  /**
   * Starts PETEP project (loads assets etc.
   *
   * @return <code>true</code> if start was successful
   */
  public boolean start() {
    try {
      // Initialize utils.
      FileUtils.setProjectDirectory(
          FileUtils.getApplicationFileAbsolutePath(arguments.getProjectPath()));

      // Load PETEP bundle.
      Bundle.getInstance().load(arguments);

      Logger.getGlobal()
          .log(Level.INFO, () -> "Loaded project " + Bundle.getInstance().getProject().getName()
              + " (" + arguments.getProjectPath() + ")!");

      return true;
    } catch (ConfigurationException e) {
      Logger.getGlobal().log(Level.SEVERE, "Project configuration exception occured!", e);
      return false;
    }
  }
}
