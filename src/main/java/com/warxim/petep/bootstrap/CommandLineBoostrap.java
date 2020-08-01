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

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.warxim.petep.Bundle;
import com.warxim.petep.core.PetepManager;
import com.warxim.petep.core.PetepState;
import com.warxim.petep.exception.ConfigurationException;

/** Bootstrap for command line mode. */
public final class CommandLineBoostrap extends PetepBootstrap {
  public CommandLineBoostrap(CommandLineArguments arguments) {
    super(arguments);
  }

  @Override
  public boolean start() {
    if (!super.start()) {
      return false;
    }

    Logger.getGlobal().info("Starting PETEP without GUI.");

    Scanner scanner = new Scanner(System.in);
    PetepManager petepManager = Bundle.getInstance().getPetepManager();
    boolean running = true;

    while (running) {
      // Start PETEP.
      petepManager.start();

      // Run input loop.
      running = runInputLoop(scanner);

      // Stop PETEP.
      petepManager.stop();

      // Wait till the PETEP core stops, so that it can start again.
      waitForPetepTermination(petepManager);
    }

    // Save PETEP. (Saving stores etc.)
    try {
      Bundle.getInstance().save();
    } catch (ConfigurationException e) {
      Logger.getGlobal().log(Level.SEVERE, "Exception occured -> ", e);
      return false;
    }

    return true;
  }

  /** Returns true if PETEP should continue running. */
  private static boolean runInputLoop(Scanner scanner) {
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();

      switch (line) {
        // Stop PETEP.
        case "stop":
        case "quit":
        case "exit":
        case "shutdown":
          return false;

        // Restart PETEP.
        case "restart":
          return true;

        // Show help.
        default:
          Logger.getGlobal().severe("Unknown command! Use 'stop' or 'restart'!");
      }
    }
    return false;
  }

  /** Waits till the PETEP core stops. */
  private static void waitForPetepTermination(PetepManager petepManager) {
    while (petepManager.getState() != PetepState.STOPPED) {
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        // Interrupted.
        break;
      }
    }
  }
}
