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
package com.warxim.petep.extension.internal.tagger.factory.internal.has_tag;

import java.io.IOException;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleConfigurator;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleData;
import com.warxim.petep.gui.dialog.Dialogs;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public final class HasTagSubruleConfigurator extends TagSubruleConfigurator {
  @FXML
  private TextField tagInput;

  public HasTagSubruleConfigurator() throws IOException {
    super("/fxml/extension/internal/tagger/factory/HasTagSubrule.fxml");
  }

  @Override
  public TagSubruleData getConfig() {
    return new HasTagData(tagInput.getText());
  }

  @Override
  public void setConfig(TagSubruleData config) {
    tagInput.setText(((HasTagData) config).getTag());
  }

  @Override
  public boolean isValid() {
    if (tagInput.getText().length() == 0) {
      Dialogs.createErrorDialog("Tag required", "You have to enter tag.");
      return false;
    }

    return true;
  }
}
