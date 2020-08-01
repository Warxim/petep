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

/** Singleton for GUI assets. */
public final class GuiBundle {
  private static GuiBundle instance = null;

  // Assets.
  private final Image petepIcon = new Image(getClass().getResourceAsStream("/img/Logo.png"));

  // GUI components.
  private ApplicationController applicationController;
  private SettingsController settingsController;

  // Guide manager.
  private final GuideManager guideManager;

  /** GUI Bundle constructor (hidden). */
  private GuiBundle() {
    guideManager = new GuideManager();
  }

  /** Returns GuiBundle instance. */
  public static GuiBundle getInstance() {
    if (instance == null) {
      instance = new GuiBundle();
    }

    return instance;
  }

  /*
   * GETTERS
   */
  public Image getPetepIcon() {
    return petepIcon;
  }

  public ApplicationController getApplicationController() {
    return applicationController;
  }

  public SettingsController getSettingsController() {
    return settingsController;
  }

  public GuideManager getGuideManager() {
    return guideManager;
  }

  /*
   * SETTERS
   */
  public void setApplicationController(ApplicationController controller) {
    applicationController = controller;
  }

  public void setSettingsController(SettingsController controller) {
    settingsController = controller;
  }
}
