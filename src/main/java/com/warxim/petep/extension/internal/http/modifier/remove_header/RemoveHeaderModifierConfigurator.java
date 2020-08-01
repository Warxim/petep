/*
 * PEnetration TEsting Proxy (PETEP)
 * 
 * Copyright (C) 2020 Michal Válka
 *
 * This program free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distribut n the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <https://www.gnu.org/licenses/>.
 */
package com.warxim.petep.extension.internal.http.modifier.remove_header;

import java.io.IOException;
import com.warxim.petep.extension.internal.http.pdu.HttpUtils;
import com.warxim.petep.extension.internal.modifier.factory.ModifierConfigurator;
import com.warxim.petep.extension.internal.modifier.factory.ModifierData;
import com.warxim.petep.gui.dialog.Dialogs;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public final class RemoveHeaderModifierConfigurator extends ModifierConfigurator {
  @FXML
  private TextField nameInput;

  public RemoveHeaderModifierConfigurator() throws IOException {
    super("/fxml/extension/internal/http/modifier/RemoveHeader.fxml");
  }

  @Override
  public ModifierData getConfig() {
    return new RemoveHeaderData(HttpUtils.formatHeaderName(nameInput.getText()));
  }

  @Override
  public void setConfig(ModifierData config) {
    nameInput.setText(((RemoveHeaderData) config).getHeader());
  }

  @Override
  public boolean isValid() {
    if (nameInput.getText().length() == 0) {
      Dialogs.createErrorDialog("Header required", "You have to enter header to remove.");
      return false;
    }

    return true;
  }
}
