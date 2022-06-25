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
package com.warxim.petep.wizard.dialog;

import com.warxim.petep.common.Constant;
import com.warxim.petep.configuration.ProjectSaver;
import com.warxim.petep.exception.ConfigurationException;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.gui.dialog.SimpleInputDialog;
import com.warxim.petep.project.Project;
import com.warxim.petep.util.FileUtils;
import com.warxim.petep.wizard.configuration.WizardExtensionsSaver;
import com.warxim.petep.wizard.project.WizardProjectDecorator;
import com.warxim.petep.wizard.project.WizardProjectExtension;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Project dialog.
 */
public abstract class ProjectDialog extends SimpleInputDialog<WizardProjectDecorator> {
    // Inputs.
    @FXML
    protected TextField pathInput;
    @FXML
    protected Button pathButton;
    @FXML
    protected TextField nameInput;
    @FXML
    protected TextArea descriptionInput;
    @FXML
    protected ToggleGroup structureToggleGroup;
    @FXML
    protected RadioButton presetRadioInput;
    @FXML
    protected RadioButton customRadioInput;
    @FXML
    protected ComboBox<String> presetInput;
    @FXML
    protected AnchorPane customPanel;

    // Extensions.
    @FXML
    protected ListView<WizardProjectExtension> extensionsList;
    protected ObservableList<WizardProjectExtension> extensions;

    /**
     * Constructs project dialog.
     * @param title Title of the dialog
     * @param okText Text of the OK button
     * @throws IOException If the dialog template could not be loaded
     */
    protected ProjectDialog(String title, String okText) throws IOException {
        super("/fxml/wizard/ProjectDialog.fxml", title, okText);
    }

    /**
     * Handles add internal extension button click.
     */
    @FXML
    protected final void onAddInternalButtonClick(ActionEvent event) {
        if (Constant.INTERNAL_EXTENSIONS.isEmpty()) {
            return;
        }

        // Choose from internal extensions.
        var dialog = new ChoiceDialog<String>(Constant.INTERNAL_EXTENSIONS.get(0), Constant.INTERNAL_EXTENSIONS);

        dialog.setTitle("Add internal extension");
        dialog.setHeaderText("Add internal extension");
        dialog.setContentText("Extension:");

        // Add extension to list.
        var result = dialog.showAndWait();
        if (result.isPresent()) {
            extensions.add(new WizardProjectExtension(result.get(), null, null));
        }
    }

    /**
     * Handles add external extension button click.
     */
    @FXML
    protected final void onAddExternalButtonClick(ActionEvent event) {
        // Choose extension file.
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Open project");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("PETEP jar extensions (*.jar)", "*.jar"));

        var file = fileChooser.showOpenDialog(null);
        if (file == null) {
            return;
        }

        // Add extension to list.
        extensions.add(new WizardProjectExtension(FileUtils.applicationRelativize(file.getPath()), null, null));
    }

    /**
     * Handles remove extension button click.
     */
    @FXML
    protected final void onRemoveButtonClick(ActionEvent event) {
        var extension = extensionsList.getSelectionModel().getSelectedItem();
        if (extension == null) {
            return;
        }

        if (!Dialogs.createYesOrNoDialog("Are you sure?",
                "Do you really want to remove the extension '" + extension.getPath()
                        + "' from the project? (Extension data stored in the configuration will be lost!)")) {
            return;
        }

        extensions.remove(extension);
    }

    /**
     * Checks if inputs are valid.
     */
    @Override
    protected final boolean isValid() {
        if (nameInput.getText().length() == 0) {
            Dialogs.createErrorDialog("Project name required", "You have to enter project name.");
            return false;
        }

        if (pathInput.getText().length() == 0) {
            Dialogs.createErrorDialog("Project path required", "You have to enter project path.");
            return false;
        }

        return true;
    }

    @FXML
    private void onPathButtonClick(ActionEvent event) {
        var directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Save project");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

        var directory = directoryChooser.showDialog(null);
        if (directory == null) {
            return;
        }

        // Project directory must be empty (if it isn't, let user choose another one).
        if (!FileUtils.isDirectoryEmpty(directory)) {
            Dialogs.createErrorDialog("Directory is not empty!",
                    "Directory for new project has to be empty!");
            return;
        }

        pathInput.setText(FileUtils.applicationRelativize(directory.getPath()));
    }

    /**
     * Saves project and its extension configuration.
     */
    protected static void saveProject(String path, Project project) throws ConfigurationException {
        var projectConfigPath = FileUtils.getApplicationFileAbsolutePath(path)
                + File.separator
                + Constant.PROJECT_CONFIG_DIRECTORY
                + File.separator
                + Constant.PROJECT_CONFIG_FILE;
        ProjectSaver.save(projectConfigPath, project);
    }

    /**
     * Saves project and its extension configuration.
     */
    protected static void saveExtensions(String path, List<WizardProjectExtension> extensions)
            throws ConfigurationException {
        var projectExtensionsPath = FileUtils.getApplicationFileAbsolutePath(path)
                + File.separator
                + Constant.PROJECT_CONFIG_DIRECTORY
                + File.separator
                + Constant.EXTENSIONS_CONFIG_FILE;
        WizardExtensionsSaver.save(projectExtensionsPath, extensions);
    }
}
