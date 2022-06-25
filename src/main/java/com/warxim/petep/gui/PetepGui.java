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

import com.sun.javafx.css.StyleManager;
import com.warxim.petep.Bundle;
import com.warxim.petep.common.Constant;
import com.warxim.petep.exception.ConfigurationException;
import com.warxim.petep.gui.common.GuiConstant;
import com.warxim.petep.gui.common.PetepApplication;
import com.warxim.petep.gui.dialog.Dialogs;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX Application class for GUI.
 */
public final class PetepGui extends PetepApplication {
    @Override
    public void start(Stage stage) {
        // Load stylesheets.
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        StyleManager.getInstance().addUserAgentStylesheet(getClass().getResource(GuiConstant.MAIN_CSS_PATH).toString());

        // Store host services inside GUI bundle
        GuiBundle.getInstance().setHostServices(getHostServices());

        // Load application GUI.
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Application.fxml"));

            var scene = new Scene(root);
            scene.getStylesheets().add(GuiConstant.MAIN_CSS_PATH);
            stage.getIcons().add(GuiBundle.getInstance().getPetepIcon());
            stage.setTitle("PETEP v" + Constant.VERSION + " (" + Bundle.getInstance().getProject().getName() + ")");
            stage.setWidth(1200);
            stage.setHeight(900);
            stage.setScene(scene);
            stage.setOnCloseRequest(this::onClose);

            stage.show();
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not load PETEP gui.", e);
        }

        processVersionCheck();
    }

    /**
     * Handles close event of the GUI window.
     * <p>Shows dialog to ask user, whether the project should be saved or not.</p>
     */
    private void onClose(WindowEvent event) {
        var decision = Dialogs.createYesOrNoOrCancelDialog("Save project", "Do you want to save project before closing PETEP?");
        if (decision.isEmpty()) {
            event.consume();
            return;
        }

        if (Boolean.TRUE.equals(decision.get())) {
            try {
                Bundle.getInstance().save();
            } catch (ConfigurationException e) {
                event.consume();
                Logger.getGlobal().log(Level.SEVERE, "Could not save project!", e);
                Dialogs.createExceptionDialog("Could not save project", "Could not save project!", e);
            }
        }
    }
}
