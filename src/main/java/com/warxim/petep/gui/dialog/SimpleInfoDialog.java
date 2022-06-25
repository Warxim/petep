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
package com.warxim.petep.gui.dialog;

import com.warxim.petep.gui.GuiBundle;
import com.warxim.petep.gui.common.GuiConstant;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Simple dialog class for creation of custom dialogs for displaying information (no input from user expected).
 */
public abstract class SimpleInfoDialog extends Stage {
    /**
     * Constructs simple info dialog from specified template.
     * @param template Path to FXML template
     * @param title Title of the dialog
     */
    protected SimpleInfoDialog(String template, String title) throws IOException {
        setTitle(title);
        setResizable(true);

        // Load template.
        var fxmlLoader = new FXMLLoader(getClass().getResource(template));
        fxmlLoader.setController(this);
        var root = new ScrollPane(fxmlLoader.load());

        root.setFitToHeight(true);
        root.setFitToWidth(true);

        var dialogScene = new Scene(root);
        dialogScene.getStylesheets().add(GuiConstant.MAIN_CSS_PATH);
        root.getStyleClass().add("simple-dialog");
        setScene(dialogScene);

        // Set icon.
        getIcons().add(GuiBundle.getInstance().getPetepIcon());
    }
}
