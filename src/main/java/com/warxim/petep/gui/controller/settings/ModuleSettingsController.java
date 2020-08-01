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
package com.warxim.petep.gui.controller.settings;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.gui.dialog.module.EditModuleDialog;
import com.warxim.petep.gui.dialog.module.NewModuleDialog;
import com.warxim.petep.module.Module;
import com.warxim.petep.module.ModuleContainer;
import com.warxim.petep.module.ModuleFactory;
import com.warxim.petep.module.ModuleFactoryManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/** Module settings controller. */
public final class ModuleSettingsController<M extends Module<F>, F extends ModuleFactory<M>>
    implements Initializable {

  // Managers.
  private final ModuleFactoryManager<F> factoryManager;
  private final ModuleContainer<M> moduleContainer;

  // Title.
  private final String title;

  // Table.
  @FXML
  private TableView<M> moduleTable;

  // Label.
  @FXML
  private Label titleLabel;

  // Columns.
  @FXML
  private TableColumn<M, String> nameColumn;
  @FXML
  private TableColumn<M, String> codeColumn;
  @FXML
  private TableColumn<M, String> moduleColumn;
  @FXML
  private TableColumn<M, String> enabledColumn;

  public ModuleSettingsController(
      String title,
      ModuleFactoryManager<F> factoryManager,
      ModuleContainer<M> moduleContainer) {
    this.title = title;
    this.factoryManager = factoryManager;
    this.moduleContainer = moduleContainer;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // Set value factories.
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
    enabledColumn.setCellValueFactory(new PropertyValueFactory<>("enabled"));
    moduleColumn.setCellValueFactory(
        cell -> new SimpleStringProperty(cell.getValue().getFactory().getName()));

    // Set items to table.
    moduleTable.setItems(FXCollections.observableList(moduleContainer.getList()));

    // Set title.
    titleLabel.setText(title);
  }

  /** Handles edit module button click. */
  @FXML
  private void onEditButtonClick(ActionEvent event) {
    M module = moduleTable.getSelectionModel().getSelectedItem();

    if (module == null) {
      return;
    }

    try {
      EditModuleDialog<M, F> dialog =
          new EditModuleDialog<>(module, factoryManager, moduleContainer);
      Optional<M> data = dialog.showAndWait();

      if (!data.isPresent() || data.get() == null) {
        return;
      }

      // Remove previous module and add new one.
      moduleContainer.replace(module, data.get());

      refreshTable();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Exception during openning of module dialog", e);
    }
  }

  /** Handles new module button click. */
  @FXML
  private void onNewButtonClick(ActionEvent event) {
    try {
      NewModuleDialog<M, F> dialog = new NewModuleDialog<>(factoryManager, moduleContainer);

      Optional<M> data = dialog.showAndWait();

      if (!data.isPresent() || data.get() == null) {
        return;
      }

      // Add new module to module container.
      moduleContainer.add(data.get());

      refreshTable();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Exception during openning of module dialog", e);
    }
  }

  /** Handles remove module button click. */
  @FXML
  private void onRemoveButtonClick(ActionEvent event) {
    M module = moduleTable.getSelectionModel().getSelectedItem();

    if (module == null) {
      return;
    }

    if (!Dialogs.createYesOrNoDialog("Are you sure?",
        "Do you really want to remove '" + module.getName() + "' from the project?")) {
      return;
    }

    // Remove module from module container.
    moduleContainer.remove(module);

    refreshTable();
  }

  /** Moves module up. */
  @FXML
  private void onMoveUpButtonClick(ActionEvent event) {
    int index = moduleTable.getSelectionModel().getSelectedIndex();

    if (index <= 0) {
      return;
    }

    moduleContainer.swap(index, index - 1);

    refreshTable();
  }

  /** Moves module down. */
  @FXML
  private void onMoveDownButtonClick(ActionEvent event) {
    int index = moduleTable.getSelectionModel().getSelectedIndex();

    if (index == -1 || index == moduleContainer.size() - 1) {
      return;
    }

    moduleContainer.swap(index, index + 1);

    refreshTable();
  }

  /** Refreshes table items. */
  private void refreshTable() {
    // modulesTable.refresh does not work - rows are not clickable.
    moduleTable.setItems(FXCollections.observableList(moduleContainer.getList()));
  }
}
