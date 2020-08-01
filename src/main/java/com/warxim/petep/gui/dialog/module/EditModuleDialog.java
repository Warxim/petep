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
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.module.Module;
import com.warxim.petep.module.ModuleContainer;
import com.warxim.petep.module.ModuleFactory;
import com.warxim.petep.module.ModuleFactoryManager;
import com.warxim.petep.persistence.Configurable;
import com.warxim.petep.persistence.Storable;
import com.warxim.petep.util.ExtensionUtils;

/** Edit module dialog. */
public final class EditModuleDialog<M extends Module<F>, F extends ModuleFactory<M>>
    extends ModuleDialog<M, F> {
  private final M oldModule;

  /** Create edit module dialog for specified module. */
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
    M module = super.obtainResult();

    processStore(module);
    processConfig(module);

    return module;
  }

  @SuppressWarnings("unchecked")
  private <S> void processStore(M module) {
    Type storeType = ExtensionUtils.getStoreType(module);

    if (storeType != null) {
      if (((Storable<S>) oldModule).saveStore() == null) {
        return;
      }

      ((Storable<S>) module).loadStore(((Storable<S>) oldModule).saveStore());
    }
  }

  @SuppressWarnings("unchecked")
  private <C> void processConfig(M module) {
    Type configType = ExtensionUtils.getConfigType(module);

    if (configType != null) {
      if (((Configurable<C>) module).saveConfig() != null) {
        return;
      }

      if (((Configurable<C>) oldModule).saveConfig() == null) {
        return;
      }

      ((Configurable<C>) module).loadConfig(((Configurable<C>) oldModule).saveConfig());
    }
  }

  @Override
  protected boolean isValid() {
    if (!super.isValid()) {
      return false;
    }

    M result = moduleContainer.get(codeInput.getText());
    if (result != null && !result.equals(oldModule)) {
      Dialogs.createErrorDialog("Code reserved",
          "You have entered code that is reserved by other module.");
      return false;
    }

    return true;
  }
}
