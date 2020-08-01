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
package com.warxim.petep.extension.internal.modifier.factory.internal.replace;

import java.io.IOException;
import com.warxim.petep.extension.internal.modifier.factory.ModifierConfigurator;
import com.warxim.petep.extension.internal.modifier.factory.ModifierData;
import com.warxim.petep.gui.control.BytesEditor;
import com.warxim.petep.gui.dialog.Dialogs;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public final class ReplacerConfigurator extends ModifierConfigurator {
  @FXML
  private TextField occurrenceInput;

  @FXML
  private BytesEditor whatInput;

  @FXML
  private BytesEditor withInput;

  public ReplacerConfigurator() throws IOException {
    super("/fxml/extension/internal/modifier/factory/Replace.fxml");

    occurrenceInput.setText("-1");
  }

  @Override
  public ModifierData getConfig() {
    return new ReplacerData(Integer.parseInt(occurrenceInput.getText()), whatInput.getBytes(),
        withInput.getBytes());
  }

  @Override
  public void setConfig(ModifierData config) {
    occurrenceInput.setText(String.valueOf(((ReplacerData) config).getOccurrence()));
    whatInput.setBytes(((ReplacerData) config).getWhat());
    withInput.setBytes(((ReplacerData) config).getWith());
  }

  @Override
  public boolean isValid() {
    if (occurrenceInput.getText().length() == 0) {
      Dialogs.createErrorDialog("Occurrence required", "You have to enter occurrence.");
      return false;
    }

    if (whatInput.getBytes().length == 0) {
      Dialogs.createErrorDialog("What required", "You have to enter what.");
      return false;
    }

    return true;
  }
}
