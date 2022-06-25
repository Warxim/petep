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

import com.warxim.petep.gui.common.DisplayFunctionStringConverter;
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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Superclass for module dialogs.
 * @param <M> Type of the module
 * @param <F> Type of the module factory
 */
public abstract class ModuleDialog<M extends Module<F>, F extends ModuleFactory<M>> extends SimpleInputDialog<M> {
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
    @FXML
    protected AnchorPane factoryPane;

    /**
     * Constructs module dialog.
     * @param title Title of the dialog
     * @param okText Text of the OK button
     * @param factoryManager Manager of module factories for working with factories
     * @param moduleContainer Module container for adding/removing modules
     * @throws IOException If the dialog template could not be loaded
     */
    protected ModuleDialog(
            String title,
            String okText,
            ModuleFactoryManager<F> factoryManager,
            ModuleContainer<M> moduleContainer) throws IOException {
        super("/fxml/tab/settings/ModuleDialog.fxml", title, okText);

        this.factoryManager = factoryManager;
        this.moduleContainer = moduleContainer;

        factoryComboBox.setConverter(new DisplayFunctionStringConverter<>(ModuleFactory::getName));

        // Add available factories to ComboBox.
        factoryComboBox.setItems(FXCollections.observableList(factoryManager.getList()));
    }

    /**
     * Validates inputs.
     */
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
        var module = factoryComboBox.getSelectionModel().getSelectedItem();
        if (module == null) {
            Dialogs.createErrorDialog("Module type required", "You have to select module type.");
            return false;
        }

        // Validate configuration pane.
        return factoryPane.getChildren().isEmpty()
                || ((ConfigPane<?>) factoryPane.getChildren().get(0)).isValid();
    }

    /**
     * Obtains new module from dialog.
     */
    @Override
    protected M obtainResult() {
        // Get selected module.
        var factory = factoryComboBox.getSelectionModel().getSelectedItem();

        // Use factory to create module.
        var module = factory.createModule(codeInput.getText(), nameInput.getText(),
                descriptionInput.getText(), enabledCheckBox.isSelected());

        // Process config pane.
        processConfigPane(module);

        return module;
    }

    /**
     * Loads configuration pane on factory change.
     */
    @FXML
    protected void onModuleChange(ActionEvent event) {
        setConfigPane(createConfigPane());
    }

    /**
     * Sets config pane to UI for configuring module-specific configuration.
     */
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

    /**
     * Creates config page using currently selected factory for configuring module-specific configuration.
     */
    protected final <T> ConfigPane<T> createConfigPane() {
        // Get selected factory.
        var factory = factoryComboBox.getValue();

        // Determine configuration type using annotations.
        var maybeConfigType = ExtensionUtils.getConfiguratorType(factory);
        if (maybeConfigType.isEmpty()) {
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

    /**
     * Processes configuration.
     */
    @SuppressWarnings("unchecked")
    private <T> void processConfigPane(M module) {
        if (!factoryPane.getChildren().isEmpty()) {
            // Get factory config pane.
            var configurator = (ConfigPane<T>) factoryPane.getChildren().get(0);

            // Load config to module.
            ((Configurable<T>) module).loadConfig(configurator.getConfig());
        }
    }
}
