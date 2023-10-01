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
package com.warxim.petep.wizard.controller;

import com.warxim.petep.Main;
import com.warxim.petep.common.Constant;
import com.warxim.petep.configuration.ProjectLoader;
import com.warxim.petep.exception.ConfigurationException;
import com.warxim.petep.gui.common.InstantCellFactory;
import com.warxim.petep.gui.dialog.AboutDialog;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.util.FileUtils;
import com.warxim.petep.wizard.configuration.WizardConfigurationLoader;
import com.warxim.petep.wizard.configuration.WizardConfigurationSaver;
import com.warxim.petep.wizard.dialog.EditProjectDialog;
import com.warxim.petep.wizard.dialog.NewProjectDialog;
import com.warxim.petep.wizard.project.WizardProjectDecorator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Wizard GUI controller.
 */
public final class WizardController implements Initializable {
    // Columns.
    @FXML
    private TableColumn<WizardProjectDecorator, String> nameColumn;
    @FXML
    private TableColumn<WizardProjectDecorator, String> descriptionColumn;
    @FXML
    private TableColumn<WizardProjectDecorator, Instant> dateColumn;
    @FXML
    private TableColumn<WizardProjectDecorator, String> pathColumn;

    // Projects.
    @FXML
    private TableView<WizardProjectDecorator> projectsTable;
    private ObservableList<WizardProjectDecorator> projects;

    /**
     * Initializes wizard.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Create observable list of projects.
        projects = FXCollections.observableList(WizardConfigurationLoader.load(
                FileUtils.getWorkingDirectoryFileAbsolutePath(Constant.WIZARD_CONFIG_FILE)
        ));

        // Initialize columns.
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("created"));
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));

        dateColumn.setCellFactory(cell -> new InstantCellFactory<>());

        // Bind projects to table.
        projectsTable.setItems(projects);
    }

    /**
     * Handles new project button click.
     */
    @FXML
    private void onNewButtonClick(ActionEvent event) {
        try {
            var newProjectDialog = new NewProjectDialog();

            var maybeProject = newProjectDialog.showAndWait();
            if (maybeProject.isEmpty()) {
                return;
            }

            projects.add(maybeProject.get());
            projectsTable.getSelectionModel().select(maybeProject.get());

            // Save wizard configuration.
            WizardConfigurationSaver.save(
                    FileUtils.getWorkingDirectoryFileAbsolutePath(Constant.WIZARD_CONFIG_FILE),
                    projects
            );
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Exception during openning of project dialog", e);
        }
    }

    /**
     * Handles edit project button click.
     */
    @FXML
    private void onEditButtonClick(ActionEvent event) {
        var project = projectsTable.getSelectionModel().getSelectedItem();
        if (project == null) {
            return;
        }

        // Show edit project dialog.
        try {
            var projectDialog = new EditProjectDialog(project);
            var maybeProject = projectDialog.showAndWait();
            if (maybeProject.isEmpty()) {
                return;
            }

            projects.remove(project);
            projects.add(maybeProject.get());
            projectsTable.getSelectionModel().select(maybeProject.get());

            // Save wizard configuration.
            WizardConfigurationSaver.save(
                    FileUtils.getWorkingDirectoryFileAbsolutePath(Constant.WIZARD_CONFIG_FILE),
                    projects
            );
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Exception during openning of project dialog", e);
        } catch (ConfigurationException e) {
            Dialogs.createExceptionDialog("Project save error", "Project could not be saved!", e);
            Logger.getGlobal().log(Level.SEVERE, "Exception during save", e);
        }
    }

    /**
     * Runs project in PETEP.
     */
    @FXML
    private void onRunButtonClick(ActionEvent event) {
        var project = projectsTable.getSelectionModel().getSelectedItem();
        if (project == null) {
            return;
        }

        // Run project.
        new Thread(() -> Main.runPetep(project.getPath(), "--from-wizard")).start();

        // Do not let JavaFX close our application, so we can start project GUI.
        Platform.setImplicitExit(false);

        // Close wizard GUI
        ((Stage) projectsTable.getScene().getWindow()).close();
    }

    /**
     * Opens project from disk and adds it to Wizard.
     */
    @FXML
    private void onOpenButtonClick(ActionEvent event) {
        // Choose project directory.
        var directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open project");
        directoryChooser.setInitialDirectory(new File(FileUtils.getWorkingDirectory()));

        var directory = directoryChooser.showDialog(null);
        if (directory == null) {
            return;
        }

        // Load project.
        try {
            var configFile = Path.of(directory.getPath())
                    .resolve(Constant.PROJECT_CONFIG_DIRECTORY)
                    .resolve(Constant.PROJECT_CONFIG_FILE)
                    .toString();

            var project = ProjectLoader.load(configFile);

            var relativizedPath = FileUtils.workingDirectoryRelativize(directory.getPath());
            var lastModified = Instant.ofEpochMilli(new File(configFile).lastModified());

            // Add project to table.
            projects.add(
                    new WizardProjectDecorator(
                            project,
                            relativizedPath,
                            lastModified));

            // Save wizard configuration.
            WizardConfigurationSaver.save(
                    FileUtils.getWorkingDirectoryFileAbsolutePath(Constant.WIZARD_CONFIG_FILE),
                    projects
            );
        } catch (ConfigurationException e) {
            Dialogs.createExceptionDialog("Project could not be loaded", "Project could not be loaded!",
                    e);
            Logger.getGlobal().log(Level.SEVERE, "Exception during load", e);
        }
    }

    /**
     * Removes project from wizard.
     */
    @FXML
    private void onRemoveButtonClick(ActionEvent event) {
        var project = projectsTable.getSelectionModel().getSelectedItem();
        if (project == null) {
            return;
        }

        if (!Dialogs.createYesOrNoDialog("Are you sure?",
                "Do you really want to remove the project '"
                        + project.getName() +
                        "' from the list? (Project will not be removed from the disk.)")) {
            return;
        }

        // Remove project from table.
        projects.remove(project);

        // Save wizard configuration.
        WizardConfigurationSaver.save(
                FileUtils.getWorkingDirectoryFileAbsolutePath(Constant.WIZARD_CONFIG_FILE),
                projects
        );
    }

    /**
     * Show about dialog on header click.
     */
    @FXML
    private void onHeaderLabelClick() {
        AboutDialog.show();
    }
}
