/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2021 Michal Válka
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
package com.warxim.petep.extension.internal.scripter.gui.rule;

import com.warxim.petep.extension.internal.scripter.helper.ScriptHelperFactory;
import com.warxim.petep.extension.internal.scripter.rule.FileScript;
import com.warxim.petep.extension.internal.scripter.rule.Script;
import com.warxim.petep.extension.internal.scripter.rule.ScriptType;
import com.warxim.petep.extension.internal.scripter.rule.StringScript;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.gui.dialog.SimpleInputDialog;
import com.warxim.petep.util.FileUtils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * Base class for edit/new script dialogs.
 */
public abstract class ScriptDialog extends SimpleInputDialog<Script> {
    @FXML
    protected TextField nameInput;
    @FXML
    protected TextArea descriptionInput;
    @FXML
    protected TextField languageInput;
    @FXML
    protected CheckBox enabledInput;
    @FXML
    protected ComboBox<ScriptType> typeInput;
    @FXML
    protected AnchorPane stringScriptPane;
    @FXML
    protected TextArea scriptInput;
    @FXML
    protected AnchorPane fileScriptPane;
    @FXML
    protected TextField pathInput;

    protected ScriptHelperFactory scriptHelperFactory;

    /**
     * Constructs script dialog.
     * @param title Title of the dialog
     * @param okText Text of the OK button
     * @param factory Factory for creation of script helpers
     * @throws IOException If the dialog template could not be loaded
     */
    protected ScriptDialog(String title, String okText, ScriptHelperFactory factory) throws IOException {
        super("/fxml/extension/internal/scripter/ScriptDialog.fxml", title, okText);
        this.scriptHelperFactory = factory;
        typeInput.setItems(FXCollections.observableList(List.of(ScriptType.STRING, ScriptType.FILE)));
    }

    @Override
    protected Script obtainResult() {
        try {
            if (typeInput.getSelectionModel().getSelectedItem() == ScriptType.STRING) {
                return new StringScript(
                        nameInput.getText(),
                        descriptionInput.getText(),
                        enabledInput.isSelected(),
                        languageInput.getText(),
                        scriptHelperFactory,
                        scriptInput.getText());
            }
            return new FileScript(
                    nameInput.getText(),
                    descriptionInput.getText(),
                    enabledInput.isSelected(),
                    languageInput.getText(),
                    scriptHelperFactory,
                    pathInput.getText());
        } catch (RuntimeException e) {
            Dialogs.createExceptionDialog(
                    "Script compilation exception",
                    "Exception occurred during script compilation!",
                    e
            );
            throw e;
        }
    }

    @Override
    protected boolean isValid() {
        return true;
    }

    /**
     * Changes script pane when script type changes.
     */
    @FXML
    protected void onTypeChange(ActionEvent event) {
        if (typeInput.getSelectionModel().getSelectedItem() == ScriptType.STRING) {
            stringScriptPane.setVisible(true);
            fileScriptPane.setVisible(false);
            return;
        }
        stringScriptPane.setVisible(false);
        fileScriptPane.setVisible(true);
    }

    /**
     * Show file chooser and lets user choose script file.
     */
    @FXML
    protected void onFileOpenButtonClick(ActionEvent event) {
        // Choose log file
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Script file");

        if (pathInput.getText().isBlank()) {
            fileChooser.setInitialDirectory(new File(FileUtils.getProjectDirectory()));
        } else {
            var temp = new File(FileUtils.getProjectFileAbsolutePath(pathInput.getText()));
            fileChooser.setInitialDirectory(temp.getParentFile());
            fileChooser.setInitialFileName(temp.getName());
        }

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JavaScript (*.js)", "*.js"),
                new FileChooser.ExtensionFilter("All files (*.*)", "*.*"));

        var file = fileChooser.showOpenDialog(null);
        if (file == null) {
            return;
        }

        pathInput.setText(Paths.get(FileUtils.getProjectDirectory())
                .toAbsolutePath()
                .relativize(Paths.get(file.getAbsolutePath()))
                .toString()
                .replace('\\', '/'));
    }
}
