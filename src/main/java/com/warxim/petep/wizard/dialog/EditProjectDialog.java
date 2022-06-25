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
import com.warxim.petep.exception.ConfigurationException;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.project.Project;
import com.warxim.petep.util.FileUtils;
import com.warxim.petep.wizard.configuration.WizardExtensionsLoader;
import com.warxim.petep.wizard.project.WizardProjectDecorator;
import javafx.collections.FXCollections;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Edit project dialog.
 */
public final class EditProjectDialog extends ProjectDialog {
    /**
     * Constructs project dialog for editing.
     * @param project Project to be edited
     * @throws IOException If the dialog template could not be loaded
     * @throws ConfigurationException If there has been problem with loading extensions
     */
    public EditProjectDialog(WizardProjectDecorator project) throws IOException, ConfigurationException {
        super("Edit project", "Save");

        // Load extensions for the project
        var list = WizardExtensionsLoader.load(
                FileUtils.getApplicationFileAbsolutePath(project.getPath())
                        + File.separator
                        + Constant.PROJECT_CONFIG_DIRECTORY
                        + File.separator
                        + Constant.EXTENSIONS_CONFIG_FILE);

        // Fill inputs
        nameInput.setText(project.getName());
        descriptionInput.setText(project.getDescription());

        extensions = FXCollections.observableArrayList(list);

        extensionsList.setItems(extensions);

        pathInput.setText(project.getPath());
        pathInput.setDisable(true);
        pathButton.setDisable(true);
        customPanel.setDisable(false);
        presetInput.setDisable(true);
        presetRadioInput.setDisable(true);
        customRadioInput.setDisable(true);
        customRadioInput.setSelected(true);
    }

    /**
     * Obtains new project decorator and extensions.
     */
    @Override
    protected WizardProjectDecorator obtainResult() {
        var newProject = new Project(nameInput.getText(), descriptionInput.getText());

        try {
            saveProject(pathInput.getText(), newProject);
            saveExtensions(pathInput.getText(), extensions);
        } catch (ConfigurationException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not save project!", e);
            Dialogs.createExceptionDialog("Project save exception", "Exception occurred during project saving!", e);
            return null;
        }

        return new WizardProjectDecorator(newProject, pathInput.getText(), Instant.now());
    }
}
