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

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import com.warxim.petep.common.Constant;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.gui.dialog.SimpleInputDialog;
import com.warxim.petep.project.Project;
import com.warxim.petep.util.FileUtils;
import com.warxim.petep.wizard.project.WizardProjectDecorator;
import com.warxim.petep.wizard.project.WizardProjectExtension;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.util.Pair;

/** Project dialog. */
public abstract class ProjectDialog
    extends SimpleInputDialog<Pair<WizardProjectDecorator, List<WizardProjectExtension>>> {
  // Project.
  protected WizardProjectDecorator project;

  // Inputs.
  @FXML
  protected TextField nameInput;
  @FXML
  protected TextArea descriptionInput;

  // Extensions.
  @FXML
  protected ListView<WizardProjectExtension> extensionsList;
  protected ObservableList<WizardProjectExtension> extensions;

  public ProjectDialog(String title, String okText) throws IOException {
    super("/fxml/wizard/Project.fxml", title, okText);
  }

  /** Handles add internal extension button click. */
  @FXML
  protected final void onAddInternalButtonClick(ActionEvent event) {
    if (Constant.INTERNAL_EXTENSIONS.isEmpty()) {
      return;
    }

    // Choose from internal extensions.
    ChoiceDialog<String> dialog =
        new ChoiceDialog<>(Constant.INTERNAL_EXTENSIONS.get(0), Constant.INTERNAL_EXTENSIONS);

    dialog.setTitle("Add internal extension");
    dialog.setHeaderText("Add internal extension");
    dialog.setContentText("Extension:");

    // Add extension to list.
    Optional<String> result = dialog.showAndWait();
    if (result.isPresent()) {
      extensions.add(new WizardProjectExtension(result.get(), null));
    }
  }

  /** Handles add external extension button click. */
  @FXML
  protected final void onAddExternalButtonClick(ActionEvent event) {
    // Choose extension file.
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open project");
    fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
    fileChooser.getExtensionFilters()
        .add(new FileChooser.ExtensionFilter("PETEP jar extensions (*.jar)", "*.jar"));

    File file = fileChooser.showOpenDialog(null);
    if (file == null) {
      return;
    }

    // Add extension to list.
    extensions
        .add(new WizardProjectExtension(FileUtils.applicationRelativize(file.getPath()), null));
  }

  /** Handles remove extension button click. */
  @FXML
  protected final void onRemoveButtonClick(ActionEvent event) {
    WizardProjectExtension extension = extensionsList.getSelectionModel().getSelectedItem();

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

  /** Checks if inputs are valid. */
  @Override
  protected final boolean isValid() {
    if (nameInput.getText().length() == 0) {
      Dialogs.createErrorDialog("Project name required", "You have to enter project name.");
      return false;
    }

    return true;
  }

  /** Obtains new project decorator and extensions. */
  @Override
  protected final Pair<WizardProjectDecorator, List<WizardProjectExtension>> obtainResult() {
    Project newProject = new Project(nameInput.getText(), descriptionInput.getText());

    project.setProject(newProject);
    project.setDate(new Date());

    return new Pair<>(project, extensions);
  }
}
