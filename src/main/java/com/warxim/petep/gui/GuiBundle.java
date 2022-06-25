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
package com.warxim.petep.gui;

import com.warxim.petep.gui.controller.ApplicationController;
import com.warxim.petep.gui.controller.settings.SettingsController;
import com.warxim.petep.gui.guide.GuideManager;
import javafx.scene.image.Image;

/**
 * Singleton for GUI assets.
 */
public final class GuiBundle {
    /**
     * Internal instance reference
     */
    private static volatile GuiBundle instance;

    /**
     * PETEP logo icon
     */
    private final Image petepIcon;

    /**
     * Manager for handling registration of guides
     */
    private final GuideManager guideManager;

    /**
     * Controller of main application window
     */

    private ApplicationController applicationController;
    /**
     * Controller of settings tab
     */
    private SettingsController settingsController;

    /**
     * GUI Bundle constructor (hidden).
     */
    private GuiBundle() {
        guideManager = new GuideManager();
        petepIcon = new Image(getClass().getResourceAsStream("/img/Logo.png"));
    }

    /**
     * Gets instance of GUI bundle.
     * @return GUI bundle instance
     */
    public static GuiBundle getInstance() {
        if (instance == null) {
            synchronized(GuiBundle.class) {
                if (instance == null) {
                    instance = new GuiBundle();
                }
            }
        }

        return instance;
    }

    /**
     * Obtains PETEP icon.
     * @return JavaFX image with PETEP icon
     */
    public Image getPetepIcon() {
        return petepIcon;
    }

    /**
     * Obtains application controller, which handles main application window UI
     * @return Application controller
     */
    public ApplicationController getApplicationController() {
        return applicationController;
    }

    /**
     * Sets application controller, which handles main application window UI
     * @param controller Application controller
     */
    public void setApplicationController(ApplicationController controller) {
        applicationController = controller;
    }

    /**
     * Obtains settings controller, which handles settings tab UI
     * @return Settings controller
     */
    public SettingsController getSettingsController() {
        return settingsController;
    }

    /**
     * Sets settings controller, which handles settings tab UI
     * @param controller Settings controller
     */
    public void setSettingsController(SettingsController controller) {
        settingsController = controller;
    }

    /**
     * Obtains Guide manager, which is used for registering and obtaining guides of various extensions.
     * @return Guide manager
     */
    public GuideManager getGuideManager() {
        return guideManager;
    }
}
