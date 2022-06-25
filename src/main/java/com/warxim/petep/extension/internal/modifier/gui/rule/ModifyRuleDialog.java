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

import com.warxim.petep.extension.internal.modifier.factory.ModifierData;
import com.warxim.petep.extension.internal.modifier.factory.ModifierFactory;
import com.warxim.petep.extension.internal.modifier.factory.ModifierFactoryManager;
import com.warxim.petep.extension.internal.modifier.rule.ModifyRule;
import com.warxim.petep.gui.common.DisplayFunctionStringConverter;
import com.warxim.petep.gui.component.ConfigPane;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.gui.dialog.SimpleInputDialog;
import com.warxim.petep.util.GuiUtils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Modify rule dialog.
 */
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

    /**
     * Constructs modify rule.
     * @param title Title of the dialog
     * @param okText Text of the OK button
     * @param factoryManager Manager of modifier factories for obtaining factories
     * @throws IOException If the dialog template could not be loaded
     */
    protected ModifyRuleDialog(String title, String okText, ModifierFactoryManager factoryManager)
            throws IOException {
        super("/fxml/extension/internal/modifier/ModifyRuleDialog.fxml", title, okText);

        enabledInput.setSelected(true);

        // Show factory name as factory input text.
        factoryInput.setConverter(new DisplayFunctionStringConverter<>(ModifierFactory::getName));

        // Show factories sorted by name
        var sortedFactories = new ArrayList<>(factoryManager.getFactories());
        sortedFactories.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        factoryInput.setItems(FXCollections.observableArrayList(sortedFactories));

        tagInput.setTooltip(GuiUtils.createTooltip(
                "Use empty string to use this modifier to all PDUs or specific tag to use this modifier only in tagged PDUs."
        ));
    }

    /**
     * On factory change, reset factory pane so that there are adequate fields.
     */
    @FXML
    protected void onFactoryChange(ActionEvent event) {
        setFactoryPane(createFactoryPane());
    }

    /**
     * Sets config pane into factory pane and anchors it.
     */
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

    /**
     * Creates factory pane for selected modifier factory.
     */
    protected final ConfigPane<ModifierData> createFactoryPane() {
        var factory = factoryInput.getSelectionModel().getSelectedItem();

        if (factory == null) {
            return null;
        }

        try {
            return factory.createConfigPane().orElse(null);
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not load factory config pane.", e);
        }

        return null;
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
        var factory = factoryInput.getSelectionModel().getSelectedItem();
        if (factory == null) {
            Dialogs.createErrorDialog("Factory required", "You have to select factory.");
            return false;
        }

        // Validate configuration pane
        return factoryPane.getChildren().isEmpty()
                || ((ConfigPane<ModifierData>) factoryPane.getChildren().get(0)).isValid();
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
