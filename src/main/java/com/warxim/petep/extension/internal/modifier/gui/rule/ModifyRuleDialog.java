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
package com.warxim.petep.extension.internal.modifier.gui.rule;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.warxim.petep.extension.internal.modifier.factory.ModifierData;
import com.warxim.petep.extension.internal.modifier.factory.ModifierFactory;
import com.warxim.petep.extension.internal.modifier.factory.ModifierFactoryManager;
import com.warxim.petep.extension.internal.modifier.rule.ModifyRule;
import com.warxim.petep.gui.component.ConfigPane;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.gui.dialog.SimpleInputDialog;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

/** Modify rule dialog. */
public abstract class ModifyRuleDialog extends SimpleInputDialog<ModifyRule> {
  @FXML
  protected ComboBox<ModifierFactory> factoryInput;

  @FXML
  protected AnchorPane factoryPane;

  @FXML
  protected TextField nameInput;

  @FXML
  protected TextArea descriptionInput;

  @FXML
  protected TextField tagInput;

  @FXML
  protected CheckBox enabledInput;

  /** Modify rule dialog constructor. */
  public ModifyRuleDialog(String title, String okText, ModifierFactoryManager factoryManager)
      throws IOException {
    super("/fxml/extension/internal/modifier/ModifyRuleDialog.fxml", title, okText);

    enabledInput.setSelected(true);

    // Show factory name as factory input text.
    factoryInput.setConverter(new StringConverter<ModifierFactory>() {
      @Override
      public String toString(ModifierFactory factory) {
        if (factory == null) {
          return "";
        }
        return factory.getName();
      }

      @Override
      public ModifierFactory fromString(String str) {
        return null;
      }
    });

    factoryInput.setItems(FXCollections.observableArrayList(factoryManager.getFactories()));
  }

  protected final void setFactoryPane(ConfigPane<ModifierData> pane) {
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

  protected final ConfigPane<ModifierData> createFactoryPane() {
    ModifierFactory factory = factoryInput.getSelectionModel().getSelectedItem();

    if (factory == null) {
      return null;
    }

    try {
      return factory.createConfigPane();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Could not load factory config pane.", e);
    }

    return null;
  }

  @FXML
  private final void onFactoryChange(ActionEvent event) {
    setFactoryPane(createFactoryPane());
  }

  @SuppressWarnings("unchecked")
  @Override
  protected final boolean isValid() {
    if (nameInput.getText().isEmpty()) {
      Dialogs.createErrorDialog("Name required", "You have to enter name.");
      return false;
    }

    // Validate tag.
    if (!tagInput.getText().isEmpty() && !tagInput.getText().matches("^[a-zA-Z0-9-_.]+$")) {
      Dialogs.createErrorDialog("Tag invalid",
          "You have entered invalid tag (allowed characters are A-Za-z0-9-_.).");
      return false;
    }

    // Validate factory
    ModifierFactory factory = factoryInput.getSelectionModel().getSelectedItem();
    if (factory == null) {
      Dialogs.createErrorDialog("Factory required", "You have to select factory.");
      return false;
    }

    // Validate configuration pane
    if (!factoryPane.getChildren().isEmpty()
        && !((ConfigPane<ModifierData>) factoryPane.getChildren().get(0)).isValid()) {
      return false;
    }

    return true;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected final ModifyRule obtainResult() {
    ModifierFactory factory = factoryInput.getSelectionModel().getSelectedItem();

    return new ModifyRule(nameInput.getText(), descriptionInput.getText(),
        enabledInput.isSelected(), tagInput.getText(), factory.createModifier(
            ((ConfigPane<ModifierData>) factoryPane.getChildren().get(0)).getConfig()));
  }
}
