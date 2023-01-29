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
package com.warxim.petep.gui.common;

import com.warxim.petep.Bundle;
import com.warxim.petep.common.Constant;
import com.warxim.petep.core.PetepState;
import com.warxim.petep.gui.GuiBundle;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.util.WebApiUtils;
import javafx.application.Application;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.Taskbar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for JavaFX application.
 */
public abstract class PetepApplication extends Application {
    @Override
    public void start(Stage stage) {
        setupIcons(stage);
    }

    /**
     * Releases resources and stops PETEP core if it is running before the application stops.
     */
    @Override
    public void stop() throws Exception {
        super.stop();

        // Close PETEP if running.
        var petepManager = Bundle.getInstance().getPetepManager();
        if (petepManager != null && petepManager.getState() != PetepState.STOPPED) {
            petepManager.stop();
        }

        Bundle.getInstance().destroy();
    }

    /**
     * Checks whether the PETEP has latest version.
     * <p>If newer version is available, notifies user during startup.</p>
     */
    protected void processVersionCheck() {
        if (Constant.VERSION.endsWith("beta")) {
            // Do not check latest stable version when using beta version
            return;
        }
        var version = WebApiUtils.getLatestVersion();
        if (!version.equals(Constant.VERSION)) {
            Dialogs.createNewVersionDialog(version);
        }
    }

    /**
     * Sets up icons for the application
     */
    protected void setupIcons(Stage stage) {
        // Set stage icons (WIN, LINUX)
        stage.getIcons().add(GuiBundle.getInstance().getPetepIcon());

        // Little workaround to set icons for macOS taskbar using AWT
        try {
            if (!Taskbar.isTaskbarSupported()) {
                return;
            }
            var iconImage = ImageIO.read(getClass().getResourceAsStream(GuiConstant.ICON_PATH));
            var taskbar = Taskbar.getTaskbar();
            taskbar.setIconImage(iconImage);
        } catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not set taskbar icon!", e);
        }
    }
}
