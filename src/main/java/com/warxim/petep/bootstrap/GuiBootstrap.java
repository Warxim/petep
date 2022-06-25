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

import com.warxim.petep.gui.GuiBundle;
import com.warxim.petep.gui.PetepGui;
import com.warxim.petep.gui.dialog.Dialogs;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.logging.Logger;

/**
 * Bootstrap for GUI mode.
 */
public final class GuiBootstrap extends PetepBootstrap {
    /**
     * Constructs bootstrap for GUI mode.
     * @param arguments Arguments for starting the application
     */
    public GuiBootstrap(CommandLineArguments arguments) {
        super(arguments);
    }

    @Override
    public void start() throws BootstrapException {
        try {
            super.start();
        } catch (BootstrapException e) {
            Platform.runLater(() -> {
                Dialogs.createExceptionDialog("Exception: " + e.getCause().getClass().getSimpleName(), e.getMessage(), e);
                Platform.setImplicitExit(true);
            });
            throw e;
        }

        Logger.getGlobal().info("Starting PETEP with GUI.");

        Dialogs.setDefaultIcon(GuiBundle.getInstance().getPetepIcon());

        if (arguments.isFromWizard()) {
            // Run PETEP using existing Application instance.
            Platform.runLater(() -> new PetepGui().start(new Stage()));

            // Re-enable implicit exit.
            Platform.runLater(() -> Platform.setImplicitExit(true));
        } else {
            // Run PETEP using new Application instance.
            new Thread(() -> Application.launch(PetepGui.class)).start();
        }
    }
}
