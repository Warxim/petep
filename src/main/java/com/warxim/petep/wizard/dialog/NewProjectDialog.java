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
import com.warxim.petep.configuration.ModulesSaver;
import com.warxim.petep.exception.ConfigurationException;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.project.Project;
import com.warxim.petep.util.FileUtils;
import com.warxim.petep.wizard.configuration.WizardProjectDirectoryCreator;
import com.warxim.petep.wizard.project.WizardProjectDecorator;
import com.warxim.petep.wizard.project.WizardProjectExtension;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * New project dialog.
 */
public final class NewProjectDialog extends ProjectDialog {
    /**
     * Constructs project dialog for creation.
     * @throws IOException If the dialog template could not be loaded
     */
    public NewProjectDialog() throws IOException {
        super("New project", "Create");

        // Initialize list of extensions.
        var list = new ArrayList<WizardProjectExtension>(Constant.INTERNAL_EXTENSIONS.size());
        for (var extension : Constant.INTERNAL_EXTENSIONS) {
            list.add(new WizardProjectExtension(extension, null, null));
        }

        extensions = FXCollections.observableArrayList(list);

        extensionsList.setItems(extensions);

        initToggles();
        initPresets();
    }

    /**
     * Obtains new project decorator and extensions.
     */
    @Override
    protected WizardProjectDecorator obtainResult() {
        var newProject = new Project(nameInput.getText(), descriptionInput.getText());
        var project = new WizardProjectDecorator(newProject, pathInput.getText(), Instant.now());
        try {
            var toggle = ((RadioButton) structureToggleGroup.getSelectedToggle()).getText();
            if (toggle.equals("Custom")) {
                createCustomProject(project.getPath(), project, extensions);
            } else {
                var preset = presetInput.getSelectionModel().getSelectedItem();
                createPresetProject(project.getPath(), project, preset);
            }
        } catch (ConfigurationException | IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not create project!", e);
            Dialogs.createExceptionDialog("Project creation exception", "Exception occurred during project creation!", e);
            return null;
        }

        return project;
    }

    /**
     * Initializes presets input.
     */
    private void initPresets() {
        var presetsDirectory = FileUtils.getApplicationFile(Constant.PRESETS_DIRECTORY);
        var presetsFiles = presetsDirectory.listFiles();
        if (presetsFiles == null || presetsFiles.length == 0) {
            // If there are no presets, disable the option and use custom project type
            presetInput.setDisable(true);
            presetRadioInput.setDisable(true);
            customRadioInput.setSelected(true);
            return;
        }
        var presets = Arrays.stream(presetsFiles)
                .filter(File::isDirectory)
                .map(File::getName)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        presetInput.setItems(presets);

        var maybeDefaultPreset = presets.stream()
                .filter(preset ->
                        preset.equalsIgnoreCase("full")
                        || preset.equalsIgnoreCase("default"))
                .findAny();
        if (maybeDefaultPreset.isEmpty()) {
            presetInput.getSelectionModel().selectFirst();
        } else {
            presetInput.getSelectionModel().select(maybeDefaultPreset.get());
        }
    }

    /**
     * Initializes toggles input.
     */
    private void initToggles() {
        structureToggleGroup.selectedToggleProperty().addListener(this::onToggleClick);
    }

    /**
     * Handles toggle click (enables/disables preset input and custom panel).
     */
    private void onToggleClick(ObservableValue<? extends Toggle> observable, Toggle previous, Toggle current) {
        var toggle = (RadioButton) structureToggleGroup.getSelectedToggle();
        if (toggle.getText().equals("Custom")) {
            customPanel.setDisable(false);
            presetInput.setDisable(true);
        } else {
            customPanel.setDisable(true);
            presetInput.setDisable(false);
        }
    }

    /**
     * Creates project using selected presets.
     */
    private static void createPresetProject(
            String path,
            WizardProjectDecorator projectDecorator,
            String presetPath)
            throws IOException, ConfigurationException {
        FileUtils.copyDirectory(
                new File(FileUtils.getApplicationFile(Constant.PRESETS_DIRECTORY), presetPath).getPath(),
                FileUtils.getWorkingDirectoryFileAbsolutePath(path)
        );

        saveProject(path, projectDecorator.getProject());
    }

    /**
     * Creates project using custom configuration.
     */
    private static void createCustomProject(
            String path,
            WizardProjectDecorator projectDecorator,
            List<WizardProjectExtension> extensions)
            throws ConfigurationException, IOException {
        // Create project directory.
        path = FileUtils.getWorkingDirectoryFileAbsolutePath(path);
        WizardProjectDirectoryCreator.create(path);

        // Save project.
        saveProject(path, projectDecorator.getProject());
        saveExtensions(path, extensions);

        var configDirectory = Path.of(path).resolve(Constant.PROJECT_CONFIG_DIRECTORY);

        // Create proxies.json if it does not exist.
        var file = configDirectory.resolve(Constant.PROXIES_CONFIG_FILE);
        if (!Files.exists(file)) {
            ModulesSaver.save(file.toString(), new ArrayList<>());
        }

        // Create interceptors-C2S.json if it does not exist.
        file = configDirectory.resolve(Constant.INTERCEPTORS_C2S_CONFIG_FILE);
        if (!Files.exists(file)) {
            ModulesSaver.save(file.toString(), new ArrayList<>());
        }

        // Create interceptors-S2C.json if it does not exist.
        file = configDirectory.resolve(Constant.INTERCEPTORS_S2C_CONFIG_FILE);
        if (!Files.exists(file)) {
            ModulesSaver.save(file.toString(), new ArrayList<>());
        }
    }
}
