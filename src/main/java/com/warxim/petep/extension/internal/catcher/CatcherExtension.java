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
package com.warxim.petep.extension.internal.catcher;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.warxim.petep.common.ContextType;
import com.warxim.petep.core.listener.PetepListener;
import com.warxim.petep.extension.Extension;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.GuiHelper;
import com.warxim.petep.helper.PetepHelper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/** Catcher extension. */
public final class CatcherExtension extends Extension {
  private GuiHelper guiHelper;
  private Node node;
  private CatcherController controller;

  /** Catcher extension constructor. */
  public CatcherExtension(String path) {
    super(path);
  }

  @Override
  public void init(ExtensionHelper helper) {
    // Register PETEP listener if GUI is enabled.
    if (helper.getContextType() == ContextType.GUI) {
      helper.registerPetepListener(new PetepListener() {
        @Override
        public void beforePrepare(PetepHelper helper) {
          // Load catcher tab.
          try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/fxml/extension/internal/catcher/Catcher.fxml"));

            controller = new CatcherController(helper);

            fxmlLoader.setController(controller);

            node = fxmlLoader.load();

            guiHelper.registerTab("Catcher", node);
          } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not load catcher GUI!", e);
          }
        }

        @Override
        public void beforeStop(PetepHelper helper) {
          // Unload catcher tab.
          guiHelper.unregisterTab(node);

          // Clear memory.
          node = null;
          controller.stop();
          controller = null;
        }
      });
    }

    helper.registerInterceptorModuleFactory(new CatcherInterceptorModuleFactory(this));
  }

  @Override
  public void initGui(GuiHelper helper) {
    guiHelper = helper;

    helper.registerGuide(new CatcherGuide());
  }

  @Override
  public String getCode() {
    return "catcher";
  }

  @Override
  public String getName() {
    return "Catcher";
  }

  @Override
  public String getDescription() {
    return "Catches PDUs and allows user to edit them before relasing (manual intercepting).";
  }

  @Override
  public String getVersion() {
    return "1.0";
  }

  public CatcherController getController() {
    return controller;
  }
}
