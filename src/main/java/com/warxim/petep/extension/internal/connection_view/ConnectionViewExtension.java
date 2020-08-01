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
package com.warxim.petep.extension.internal.connection_view;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.warxim.petep.common.ContextType;
import com.warxim.petep.core.listener.PetepListener;
import com.warxim.petep.extension.Extension;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.GuiHelper;
import com.warxim.petep.helper.PetepHelper;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

/** Connection view extension. */
public final class ConnectionViewExtension extends Extension {
  private ConnectionViewsController controller;

  /** Connection view extension constructor. */
  public ConnectionViewExtension(String path) {
    super(path);
  }

  @Override
  public void init(ExtensionHelper helper) {
    if (helper.getContextType() != ContextType.GUI) {
      return;
    }

    helper.registerPetepListener(new PetepListener() {
      @Override
      public void afterPrepare(PetepHelper helper) {
        Platform.runLater(() -> controller.load(helper));
      }

      @Override
      public void beforeStop(PetepHelper helper) {
        Platform.runLater(controller::unload);
      }
    });
  }

  @Override
  public void initGui(GuiHelper helper) {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(
          getClass().getResource("/fxml/extension/internal/connection_view/ConnectionViews.fxml"));
      controller = new ConnectionViewsController();
      fxmlLoader.setController(controller);

      helper.registerTab("Connections", fxmlLoader.load());
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Could not load connection view!", e);
    }

    helper.registerGuide(new ConnectionViewGuide());
  }

  @Override
  public String getCode() {
    return "connection_view";
  }

  @Override
  public String getName() {
    return "Connection View";
  }

  @Override
  public String getDescription() {
    return "Simple connection view.";
  }

  @Override
  public String getVersion() {
    return "1.0";
  }
}
