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

import com.warxim.petep.bootstrap.*;
import com.warxim.petep.common.ContextType;
import com.warxim.petep.exception.CommandLineArgumentsException;
import com.warxim.petep.wizard.PetepWizard;
import javafx.application.Application;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application main class.
 */
public final class Main {
    private Main() {
    }

    /**
     * Main entry method for the application.
     * @param args Arguments provided by the user in the command line
     */
    public static void main(final String... args) {
        if (args.length == 0) {
            runWizard();
        } else {
            runPetep(args);
        }
    }

    /**
     * Runs PETEP project with specified arguments.
     * @param args Arguments provided by the user in the command line
     */
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

        try {
            bootstrap.start();
        } catch (BootstrapException e) {
            Logger.getGlobal().log(Level.SEVERE, "Bootstrap exception occurred!", e);
        }
    }

    /**
     * Runs project wizard.
     */
    public static void runWizard() {
        Logger.getGlobal().info("Running PETEP wizard.");

        new Thread(() -> Application.launch(PetepWizard.class)).start();
    }
}
