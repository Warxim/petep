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
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.module.Module;
import com.warxim.petep.module.ModuleContainer;
import com.warxim.petep.module.ModuleFactory;
import com.warxim.petep.module.ModuleFactoryManager;

/** New module dialog. */
public final class NewModuleDialog<M extends Module<F>, F extends ModuleFactory<M>>
    extends ModuleDialog<M, F> {
  public NewModuleDialog(ModuleFactoryManager<F> factoryManager, ModuleContainer<M> moduleContainer)
      throws IOException {
    super("New module", "Save", factoryManager, moduleContainer);
  }

  @Override
  protected boolean isValid() {
    if (!super.isValid()) {
      return false;
    }

    // Determine whether the code is already in use.
    if (moduleContainer.contains(codeInput.getText())) {
      Dialogs.createErrorDialog("Code reserved",
          "You have entered code that is reserved by other module.");
      return false;
    }

    return true;
  }
}
