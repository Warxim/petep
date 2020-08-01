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

import com.warxim.petep.common.ContextType;
import com.warxim.petep.exception.CommandLineArgumentsException;

/** Command line arguments. */
public final class CommandLineArguments {
  private final String[] arguments;
  private final String projectPath;
  private ContextType contextType;
  private boolean fromWizard;

  public static final String HELP = "petep project_dir [--nogui]";

  /**
   * Command line arguments constructor
   *
   * @param arguments CMD arguments from main method
   */
  public CommandLineArguments(String[] arguments) throws CommandLineArgumentsException {
    if (arguments.length < 1) {
      throw new CommandLineArgumentsException("Not enough parameters!");
    }

    this.arguments = arguments;

    // Use the first argument as project path.
    projectPath = arguments[0];

    // Detect context type and whether the application starts from wizard.
    contextType = ContextType.GUI;
    fromWizard = false;

    for (int i = 1; i < arguments.length; ++i) {
      String argument = arguments[i].toLowerCase();

      if ("--nogui".equals(argument)) {
        contextType = ContextType.COMMAND_LINE;
      } else if ("--from-wizard".equals(argument)) {
        fromWizard = true;
      }
    }
  }

  /** Returns context type of PETEP (GUI / COMMAND_LINE). */
  public ContextType getContextType() {
    return contextType;
  }

  /** Returns command line arguments. */
  public String[] getArguments() {
    return arguments;
  }

  /** Returns project path. */
  public String getProjectPath() {
    return projectPath;
  }

  /**
   * Returns true if project is started from wizard. (JavaFX application cannot be created multiple
   * times.)
   */
  public boolean isFromWizard() {
    return fromWizard;
  }
}
