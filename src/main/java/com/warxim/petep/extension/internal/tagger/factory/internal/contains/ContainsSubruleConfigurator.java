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
package com.warxim.petep.extension.internal.tagger.factory.internal.contains;

import java.io.IOException;
import java.nio.charset.Charset;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleConfigurator;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleData;
import com.warxim.petep.gui.control.BytesEditor;
import com.warxim.petep.gui.dialog.Dialogs;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public final class ContainsSubruleConfigurator extends TagSubruleConfigurator {
  @FXML
  private BytesEditor dataInput;
  @FXML
  private TextField indexInput;

  public ContainsSubruleConfigurator() throws IOException {
    super("/fxml/extension/internal/tagger/factory/ContainsSubrule.fxml");
  }

  @Override
  public TagSubruleData getConfig() {
    return new ContainsData(dataInput.getBytes(), dataInput.getCharset().name(),
        Integer.parseInt(indexInput.getText()));
  }

  @Override
  public void setConfig(TagSubruleData config) {
    dataInput.setData(((ContainsData) config).getData(),
        Charset.forName(((ContainsData) config).getCharset()));
    indexInput.setText(String.valueOf(((ContainsData) config).getIndex()));
  }

  @Override
  public boolean isValid() {
    if (dataInput.getBytes().length == 0) {
      Dialogs.createErrorDialog("Data required", "You have enter data.");
      return false;
    }

    try {
      int index = Integer.parseInt(indexInput.getText());
      if (index < 0 && index != -1) {
        Dialogs.createErrorDialog("Invalid index",
            "Index has to be -1 or number greater or equal to 0.");
        return false;
      }
    } catch (NumberFormatException e) {
      Dialogs.createErrorDialog("Invalid index",
          "Index has to be -1 or number greater or equal to 0.");
      return false;
    }

    return true;
  }
}
