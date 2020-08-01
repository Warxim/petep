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

import com.warxim.petep.Bundle;
import com.warxim.petep.common.Constant;
import com.warxim.petep.core.PetepManager;
import com.warxim.petep.core.PetepState;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.util.WebApiUtils;
import javafx.application.Application;
import javafx.stage.Stage;

public class PetepApplication extends Application {
  @Override
  public void start(Stage stage) throws Exception {
    throw new UnsupportedOperationException("Application.start() method is not implemented.");
  }

  @Override
  public void stop() throws Exception {
    super.stop();

    // Close PETEP if running.
    PetepManager petepManager = Bundle.getInstance().getPetepManager();
    if (petepManager != null && petepManager.getState() != PetepState.STOPPED) {
      Bundle.getInstance().getPetepManager().stop();
    }
  }

  protected void processVersionCheck() {
    String version = WebApiUtils.getLatestVersion();
    if (!version.equals(Constant.VERSION)) {
      Dialogs.createNewVersionDialog(version);
    }
  }
}
