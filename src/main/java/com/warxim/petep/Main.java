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
package com.warxim.petep;

import java.util.logging.Logger;
import com.warxim.petep.bootstrap.CommandLineArguments;
import com.warxim.petep.bootstrap.CommandLineBoostrap;
import com.warxim.petep.bootstrap.GuiBootstrap;
import com.warxim.petep.bootstrap.PetepBootstrap;
import com.warxim.petep.common.ContextType;
import com.warxim.petep.exception.CommandLineArgumentsException;
import com.warxim.petep.wizard.PetepWizard;
import javafx.application.Application;

/** Application main class. */
public final class Main {
  private Main() {}

  public static void main(final String... args) {
    if (args.length == 0) {
      runWizard();
    } else {
      runPetep(args);
    }
  }

  /** Runs PETEP project with specified arguments. */
  public static void runPetep(final String... args) {
    CommandLineArguments arguments;

    // Parse arguments from command line.
    try {
      arguments = new CommandLineArguments(args);
    } catch (CommandLineArgumentsException e) {
      Logger.getGlobal().severe(e.getMessage());
      Logger.getGlobal().info(CommandLineArguments.HELP);
      return;
    }

    // Choose bootstrap type.
    PetepBootstrap bootstrap;
    if (arguments.getContextType() == ContextType.GUI) {
      bootstrap = new GuiBootstrap(arguments);
    } else {
      bootstrap = new CommandLineBoostrap(arguments);
    }

    if (!bootstrap.start()) {
      Logger.getGlobal().severe("PETEP start failed!");
    }
  }

  /** Runs project wizard. */
  public static void runWizard() {
    Logger.getGlobal().info("Running PETEP wizard.");

    new Thread(() -> Application.launch(PetepWizard.class)).start();
  }
}
