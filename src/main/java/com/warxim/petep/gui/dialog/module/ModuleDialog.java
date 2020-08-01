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
package com.warxim.petep.gui.dialog.module;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.warxim.petep.gui.component.ConfigPane;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.gui.dialog.SimpleInputDialog;
import com.warxim.petep.module.Module;
import com.warxim.petep.module.ModuleContainer;
import com.warxim.petep.module.ModuleFactory;
import com.warxim.petep.module.ModuleFactoryManager;
import com.warxim.petep.persistence.Configurable;
import com.warxim.petep.persistence.Configurator;
import com.warxim.petep.util.ExtensionUtils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

/** Superclass for module dialogs. */
public abstract class ModuleDialog<M extends Module<F>, F extends ModuleFactory<M>>
    extends SimpleInputDialog<M> {
  // Managers.
  protected final ModuleFactoryManager<F> factoryManager;
  protected final ModuleContainer<M> moduleContainer;

  // Inputs.
  @FXML
  protected ComboBox<F> factoryComboBox;

  @FXML
  protected TextField codeInput;
  @FXML
  protected TextField nameInput;
  @FXML
  protected TextArea descriptionInput;
  @FXML
  protected CheckBox enabledCheckBox;

  // Configuration pane.
  @FXML
  protected AnchorPane factoryPane;

  public ModuleDialog(
      String title,
      String okText,
      ModuleFactoryManager<F> factoryManager,
      ModuleContainer<M> moduleContainer) throws IOException {
    super("/fxml/tab/settings/ModuleDialog.fxml", title, okText);

    this.factoryManager = factoryManager;
    this.moduleContainer = moduleContainer;

    // Create converter to display factory name instead of toString().
    factoryComboBox.setConverter(new StringConverter<F>() {
      @Override
      public String toString(F factory) {
        return factory == null ? "" : factory.getName();
      }

      @Override
      public F fromString(String str) {
        return null;
      }
    });

    // Add available factories to ComboBox.
    factoryComboBox.setItems(FXCollections.observableList(factoryManager.getList()));
  }

  protected final void setConfigPane(ConfigPane<?> pane) {
    if (pane == null) {
      // Clear factory pane if config pane does not exist.
      factoryPane.getChildren().clear();
      return;
    }

    AnchorPane.setLeftAnchor(pane, 0D);
    AnchorPane.setRightAnchor(pane, 0D);

    // Add config pane to factory pane.
    factoryPane.getChildren().setAll(pane);
  }

  protected final <T> ConfigPane<T> createConfigPane() {
    // Get selected factory.
    F factory = factoryComboBox.getValue();

    // Determine configuration type using annotations.
    Type configType = ExtensionUtils.getConfiguratorType(factory);
    if (configType == null) {
      return null;
    }

    try {
      // Create configuration pane using factory.
      @SuppressWarnings("unchecked")
      var configPane = ((Configurator<T>) factory).createConfigPane();

      // Return created configuration pane.
      return configPane;
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Exception during openning of module dialog", e);
    }

    // Return null if it was not possible to create config pane.
    return null;
  }

  /** Validates inputs. */
  @Override
  protected boolean isValid() {
    // Validate code.
    if (!codeInput.getText().matches("^[a-zA-Z0-9-_.]+$")) {
      Dialogs.createErrorDialog("Code required",
          "You have to enter code (allowed characters are A-Za-z0-9-_.).");
      return false;
    }

    // Validate name.
    if (nameInput.getText().isBlank()) {
      Dialogs.createErrorDialog("Name required", "You have to enter name.");
      return false;
    }

    // Validate module factory.
    F module = factoryComboBox.getSelectionModel().getSelectedItem();
    if (module == null) {
      Dialogs.createErrorDialog("Module type required", "You have to select module type.");
      return false;
    }

    // Validate configuration pane.
    return factoryPane.getChildren().isEmpty()
        || ((ConfigPane<?>) factoryPane.getChildren().get(0)).isValid();
  }

  /** Obtains new module from dialog. */
  @Override
  protected M obtainResult() {
    // Get selected module.
    F factory = factoryComboBox.getSelectionModel().getSelectedItem();

    // Use factory to create module.
    M module = factory.createModule(codeInput.getText(), nameInput.getText(),
        descriptionInput.getText(), enabledCheckBox.isSelected());

    // Process config pane.
    processConfigPane(module);

    return module;
  }

  /** Loads configuration pane on factory change. */
  @FXML
  private final void onModuleChange(ActionEvent event) {
    setConfigPane(createConfigPane());
  }

  /** Processes configuration. */
  @SuppressWarnings("unchecked")
  private <T> void processConfigPane(M module) {
    if (!factoryPane.getChildren().isEmpty()) {
      // Get factory config pane.
      ConfigPane<T> configurator = (ConfigPane<T>) factoryPane.getChildren().get(0);

      // Load config to module.
      ((Configurable<T>) module).loadConfig(configurator.getConfig());
    }
  }
}
