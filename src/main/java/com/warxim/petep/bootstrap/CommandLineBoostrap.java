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
import com.warxim.petep.core.PetepManager;
import com.warxim.petep.core.PetepState;
import com.warxim.petep.exception.ConfigurationException;

import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Bootstrap for command line mode.
 * <p>
 *     Starts PETEP in NOGUI mode (automatically starts PETEP core and allows user only to stop/restart the core.)
 * </p>
 * <p>
 *     No configuration can be done using the command line, everything has to be done using GUI or manually in json files.
 * </p>
 */
public final class CommandLineBoostrap extends PetepBootstrap {
    /**
     * Constructs bootstrap for command line mode.
     * @param arguments Arguments for starting the application
     */
    public CommandLineBoostrap(CommandLineArguments arguments) {
        super(arguments);
    }

    @Override
    public void start() throws BootstrapException {
        super.start();

        Logger.getGlobal().info("Starting PETEP without GUI.");

        var scanner = new Scanner(System.in, Charset.defaultCharset());
        var petepManager = Bundle.getInstance().getPetepManager();
        var running = true;

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
            throw new BootstrapException("Exception occurred during saving of the project!", e);
        }

        Bundle.getInstance().destroy();
    }

    /**
     * Returns true if PETEP should continue running.
     */
    private static boolean runInputLoop(Scanner scanner) {
        while (scanner.hasNextLine()) {
            var line = scanner.nextLine();

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

    /**
     * Waits till the PETEP core stops.
     */
    private static void waitForPetepTermination(PetepManager petepManager) {
        while (petepManager.getState() != PetepState.STOPPED) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // Interrupted.
                break;
            }
        }
    }
}
