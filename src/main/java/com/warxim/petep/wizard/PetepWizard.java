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
package com.warxim.petep.wizard;

import com.sun.javafx.css.StyleManager;
import com.warxim.petep.common.Constant;
import com.warxim.petep.gui.GuiBundle;
import com.warxim.petep.gui.common.GuiConstant;
import com.warxim.petep.gui.common.PetepApplication;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.wizard.controller.WizardController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX Application class for GUI.
 */
public final class PetepWizard extends PetepApplication {
    @Override
    public void start(Stage stage) {
        // Load stylesheets.
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        StyleManager.getInstance()
                .addUserAgentStylesheet(getClass().getResource(GuiConstant.MAIN_CSS_PATH).toString());

        // Load wizard GUI.
        try {
            var fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/wizard/Wizard.fxml"));
            fxmlLoader.setController(new WizardController());
            Parent root = fxmlLoader.load();

            var scene = new Scene(root);
            scene.getStylesheets().add(GuiConstant.MAIN_CSS_PATH);
            stage.getIcons().add(GuiBundle.getInstance().getPetepIcon());
            stage.setTitle("PETEP Project Wizard v" + Constant.VERSION);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not load PETEP gui. ", e);
        }

        Dialogs.setDefaultIcon(GuiBundle.getInstance().getPetepIcon());

        processVersionCheck();
    }
}
