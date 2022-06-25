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

import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.module.Module;
import com.warxim.petep.module.ModuleContainer;
import com.warxim.petep.module.ModuleFactory;
import com.warxim.petep.module.ModuleFactoryManager;
import com.warxim.petep.persistence.Configurable;
import com.warxim.petep.persistence.Storable;
import com.warxim.petep.util.ExtensionUtils;

import java.io.IOException;

/**
 * Edit module dialog.
 * @param <M> Type of the module
 * @param <F> Type of the module factory
 */
public final class EditModuleDialog<M extends Module<F>, F extends ModuleFactory<M>> extends ModuleDialog<M, F> {
    private final M oldModule;

    /**
     * Constructs module dialog for editing.
     * @param module Module to be edited
     * @param factoryManager Manager of module factories for working with factories
     * @param moduleContainer Module container for adding/removing modules
     * @throws IOException If the dialog template could not be loaded
     */
    public EditModuleDialog(
            M module,
            ModuleFactoryManager<F> factoryManager,
            ModuleContainer<M> moduleContainer) throws IOException {
        super("Edit module", "Save", factoryManager, moduleContainer);

        this.oldModule = module;

        // Select module and disable combo box.
        factoryComboBox.getSelectionModel().select(module.getFactory());
        factoryComboBox.setDisable(true);

        // Fill input fields.
        codeInput.setText(module.getCode());
        nameInput.setText(module.getName());
        descriptionInput.setText(module.getDescription());
        enabledCheckBox.setSelected(module.isEnabled());

        // Load config pane.
        var pane = createConfigPane();
        if (pane == null) {
            return;
        }
        setConfigPane(pane);

        // Get configuration from module.
        var config = ((Configurable<?>) module).saveConfig();
        if (config == null) {
            return;
        }

        // Set config to pane.
        pane.setConfig(config);
    }

    @Override
    protected M obtainResult() {
        var module = super.obtainResult();

        loadStoreFromOldModuleToNew(module);
        loadConfigFromOldModuleToNew(module);

        return module;
    }

    @Override
    protected boolean isValid() {
        if (!super.isValid()) {
            return false;
        }

        var maybeResult = moduleContainer.get(codeInput.getText());
        if (maybeResult.isPresent() && !maybeResult.get().equals(oldModule)) {
            Dialogs.createErrorDialog(
                    "Code reserved",
                    "You have entered code that is reserved by other module.");
            return false;
        }

        return true;
    }

    /**
     * Loads store from old module into new one.
     */
    @SuppressWarnings("unchecked")
    private <S> void loadStoreFromOldModuleToNew(M module) {
        var storeType = ExtensionUtils.getStoreType(module);
        if (storeType.isEmpty()) {
            return;
        }

        var store = ((Storable<S>) oldModule).saveStore();
        if (store == null) {
            return;
        }

        ((Storable<S>) module).loadStore(store);
    }

    /**
     * Loads config from old module into new one.
     */
    @SuppressWarnings("unchecked")
    private <C> void loadConfigFromOldModuleToNew(M module) {
        var configType = ExtensionUtils.getConfigType(module);
        if (configType.isEmpty()) {
            return;
        }

        if (((Configurable<C>) module).saveConfig() != null) {
            return;
        }

        var config = ((Configurable<C>) oldModule).saveConfig();
        if (config == null) {
            return;
        }

        ((Configurable<C>) module).loadConfig(config);
    }
}
