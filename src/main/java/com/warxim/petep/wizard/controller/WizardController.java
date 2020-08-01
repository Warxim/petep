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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.warxim.petep.Main;
import com.warxim.petep.configuration.ModulesSaver;
import com.warxim.petep.configuration.ProjectLoader;
import com.warxim.petep.configuration.ProjectSaver;
import com.warxim.petep.exception.ConfigurationException;
import com.warxim.petep.gui.dialog.AboutDialog;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.project.Project;
import com.warxim.petep.util.FileUtils;
import com.warxim.petep.wizard.configuration.WizardConfigurationLoader;
import com.warxim.petep.wizard.configuration.WizardConfigurationSaver;
import com.warxim.petep.wizard.configuration.WizardExtensionsSaver;
import com.warxim.petep.wizard.configuration.WizardProjectDirectoryCreator;
import com.warxim.petep.wizard.dialog.EditProjectDialog;
import com.warxim.petep.wizard.dialog.NewProjectDialog;
import com.warxim.petep.wizard.project.WizardProjectDecorator;
import com.warxim.petep.wizard.project.WizardProjectExtension;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

/** Wizard GUI controller. */
public final class WizardController implements Initializable {
  private static final String WIZARD_FILE = "petep.json";

  // Columns.
  @FXML
  private TableColumn<WizardProjectDecorator, String> nameColumn;
  @FXML
  private TableColumn<WizardProjectDecorator, String> descriptionColumn;
  @FXML
  private TableColumn<WizardProjectDecorator, Date> dateColumn;
  @FXML
  private TableColumn<WizardProjectDecorator, String> pathColumn;

  // Projects.
  @FXML
  private TableView<WizardProjectDecorator> projectsTable;
  private ObservableList<WizardProjectDecorator> projects;

  /** Initializes wizard. */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // Create observable list of projects.
    projects = FXCollections.observableList(WizardConfigurationLoader.load(WIZARD_FILE));

    // Initialize columns.
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
    pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));

    dateColumn.setCellFactory(tc -> new TableCell<WizardProjectDecorator, Date>() {
      private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

      @Override
      protected void updateItem(Date date, boolean empty) {
        super.updateItem(date, empty);

        setText(empty ? null : format.format(date));
      }
    });

    // Bind projects to table.
    projectsTable.setItems(projects);
  }

  /** Handles new project button click. */
  @FXML
  private void onNewButtonClick(ActionEvent event) {
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle("Save project");
    directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

    // Do until the user enters correct values.
    do {
      // Choose project directory.
      File directory = directoryChooser.showDialog(null);
      if (directory == null) {
        return;
      }

      // Project directory must be empty (if it isn't, let user choose another one).
      if (!FileUtils.isDirectoryEmpty(directory)) {
        Dialogs.createErrorDialog("Directory is not empty!",
            "Directory for new project has to be empty!");
        continue;
      }

      try {
        var project = showNewProjectDialog(directory.toPath());

        if (project == null) {
          return;
        }

        createProject(directory.getPath(), project);

        return;
      } catch (IOException | ConfigurationException e) {
        Dialogs.createExceptionDialog("Exception occured",
            "Exception occured during project creation!", e);
        Logger.getGlobal().log(Level.SEVERE, "Exception during project creation", e);
      }

    } while (true);
  }

  /** Handles edit project button click. */
  @FXML
  private void onEditButtonClick(ActionEvent event) {
    WizardProjectDecorator project = projectsTable.getSelectionModel().getSelectedItem();

    if (project == null) {
      return;
    }

    // Show edit project dialog.
    try {
      EditProjectDialog projectDialog = new EditProjectDialog(project);

      var data = projectDialog.showAndWait();

      if (!data.isPresent() || data.get() == null) {
        return;
      }

      // Save project.
      saveProject(project.getPath(), data.get().getKey().getProject(), data.get().getValue());

      // Refresh projects table.
      projectsTable.refresh();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Exception during openning of project dialog", e);
    } catch (ConfigurationException e) {
      Dialogs.createExceptionDialog("Project save error", "Project could not be saved!", e);
      Logger.getGlobal().log(Level.SEVERE, "Exception during save", e);
    }
  }

  /** Saves project and its extension configuration. */
  private void saveProject(String path, Project project, List<WizardProjectExtension> extensions)
      throws ConfigurationException {
    ProjectSaver.save(FileUtils.getApplicationFileAbsolutePath(path) + File.separator
        + com.warxim.petep.common.Constant.PROJECT_CONFIG_DIRECTORY + File.separator
        + com.warxim.petep.common.Constant.PROJECT_CONFIG_FILE, project);

    WizardExtensionsSaver.save(FileUtils.getApplicationFileAbsolutePath(path) + File.separator
        + com.warxim.petep.common.Constant.PROJECT_CONFIG_DIRECTORY + File.separator
        + com.warxim.petep.common.Constant.EXTENSIONS_CONFIG_FILE, extensions);
  }

  /** Runs project in PETEP. */
  @FXML
  private void onRunButtonClick(ActionEvent event) {
    WizardProjectDecorator project = projectsTable.getSelectionModel().getSelectedItem();

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

  /** Opens project from disk and adds it to Wizard. */
  @FXML
  private void onOpenButtonClick(ActionEvent event) {
    // Choose project directory.
    DirectoryChooser directoryChooser = new DirectoryChooser();
    directoryChooser.setTitle("Open project");
    directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

    File directory = directoryChooser.showDialog(null);
    if (directory == null) {
      return;
    }

    // Load project.
    try {
      String configFile = directory.getPath() + File.separator
          + com.warxim.petep.common.Constant.PROJECT_CONFIG_DIRECTORY + File.separator
          + com.warxim.petep.common.Constant.PROJECT_CONFIG_FILE;

      Project project = ProjectLoader.load(configFile);

      // Add project to table.
      projects.add(
          new WizardProjectDecorator(project, FileUtils.applicationRelativize(directory.getPath()),
              new Date(new File(configFile).lastModified())));

      // Save wizard configuration.
      WizardConfigurationSaver.save(WIZARD_FILE, projects);
    } catch (ConfigurationException e) {
      Dialogs.createExceptionDialog("Project could not be loaded", "Project could not be loaded!",
          e);
      Logger.getGlobal().log(Level.SEVERE, "Exception during load", e);
    }
  }

  /** Removes project from wizard. */
  @FXML
  private void onRemoveButtonClick(ActionEvent event) {
    WizardProjectDecorator project = projectsTable.getSelectionModel().getSelectedItem();

    if (project == null) {
      return;
    }

    if (!Dialogs.createYesOrNoDialog("Are you sure?", "Do you really want to remove the project '"
        + project.getName() + "' from the list? (Project will not be removed from the disk.)")) {
      return;
    }

    // Remove project from table.
    projects.remove(project);

    // Save wizard configuration.
    WizardConfigurationSaver.save(WIZARD_FILE, projects);
  }

  /** Show about dialog on header click. */
  @FXML
  private void onHeaderLabelClick() {
    AboutDialog.show();
  }

  private Pair<WizardProjectDecorator, List<WizardProjectExtension>> showNewProjectDialog(Path path)
      throws IOException {
    NewProjectDialog projectDialog = new NewProjectDialog(FileUtils.applicationRelativize(path));

    var data = projectDialog.showAndWait();

    return data.isPresent() ? data.get() : null;
  }

  private void createProject(
      String path,
      Pair<WizardProjectDecorator, List<WizardProjectExtension>> data)
      throws ConfigurationException, IOException {
    // Create project directory.
    WizardProjectDirectoryCreator.create(path);

    // Save project.
    saveProject(path, data.getKey().getProject(), data.getValue());

    String configDirectory = path + File.separator
        + com.warxim.petep.common.Constant.PROJECT_CONFIG_DIRECTORY + File.separator;

    // Create proxies.json if it does not exist.
    String file = configDirectory + com.warxim.petep.common.Constant.PROXIES_CONFIG_FILE;
    if (!new File(file).exists()) {
      ModulesSaver.save(file, new ArrayList<>());
    }

    // Create interceptors-C2S.json if it does not exist.
    file = configDirectory + com.warxim.petep.common.Constant.INTERCEPTORS_C2S_CONFIG_FILE;
    if (!new File(file).exists()) {
      ModulesSaver.save(file, new ArrayList<>());
    }

    // Create interceptors-S2C.json if it does not exist.
    file = configDirectory + com.warxim.petep.common.Constant.INTERCEPTORS_S2C_CONFIG_FILE;
    if (!new File(file).exists()) {
      ModulesSaver.save(file, new ArrayList<>());
    }

    // Add project to table.
    projects.add(data.getKey());

    // Save wizard configuration.
    WizardConfigurationSaver.save(WIZARD_FILE, projects);
  }
}
