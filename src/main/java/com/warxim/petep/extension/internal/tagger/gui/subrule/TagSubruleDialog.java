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
package com.warxim.petep.extension.internal.tagger.gui.subrule;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.warxim.petep.extension.internal.tagger.factory.TagSubrule;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleData;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleFactory;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleFactoryManager;
import com.warxim.petep.gui.component.ConfigPane;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.gui.dialog.SimpleInputDialog;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

/** Tag subrule dialog. */
public abstract class TagSubruleDialog extends SimpleInputDialog<TagSubrule> {
  @FXML
  protected ComboBox<TagSubruleFactory> factoryInput;

  @FXML
  protected AnchorPane factoryPane;

  /** Tag subrule dialog constructor. */
  public TagSubruleDialog(String title, String okText, TagSubruleFactoryManager factoryManager)
      throws IOException {
    super("/fxml/extension/internal/tagger/TagSubruleDialog.fxml", title, okText);

    // Show factory name as factory input text.
    factoryInput.setConverter(new StringConverter<TagSubruleFactory>() {
      @Override
      public String toString(TagSubruleFactory factory) {
        if (factory == null) {
          return "";
        }
        return factory.getName();
      }

      @Override
      public TagSubruleFactory fromString(String str) {
        return null;
      }
    });

    factoryInput.setItems(FXCollections.observableArrayList(factoryManager.getFactories()));
  }

  protected final void setFactoryPane(ConfigPane<TagSubruleData> pane) {
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

  protected final ConfigPane<TagSubruleData> createFactoryPane() {
    TagSubruleFactory factory = factoryInput.getSelectionModel().getSelectedItem();

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

  @SuppressWarnings("unchecked")
  @Override
  protected final boolean isValid() {
    // Validate factory
    TagSubruleFactory factory = factoryInput.getSelectionModel().getSelectedItem();
    if (factory == null) {
      Dialogs.createErrorDialog("Factory required", "You have to select factory.");
      return false;
    }

    // Validate configuration pane
    if (!factoryPane.getChildren().isEmpty()
        && !((ConfigPane<TagSubruleData>) factoryPane.getChildren().get(0)).isValid()) {
      return false;
    }

    return true;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected final TagSubrule obtainResult() {
    TagSubruleFactory factory = factoryInput.getSelectionModel().getSelectedItem();

    if (factoryPane.getChildren().isEmpty()) {
      return factory.createSubrule(null);
    } else {
      return factory.createSubrule(
          ((ConfigPane<TagSubruleData>) factoryPane.getChildren().get(0)).getConfig());
    }
  }

  @FXML
  private final void onFactoryChange(ActionEvent event) {
    setFactoryPane(createFactoryPane());
  }
}
