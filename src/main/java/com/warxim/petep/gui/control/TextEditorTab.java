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
package com.warxim.petep.gui.control;

import java.io.IOException;
import java.nio.charset.Charset;
import com.warxim.petep.common.Constant;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.util.GuiUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;

@PetepAPI
public class TextEditorTab extends Tab implements BytesEditorTab {
  private Charset charset;

  @FXML
  private TextArea textInput;

  public TextEditorTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/control/TextEditorTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.setClassLoader(getClass().getClassLoader());
    loader.load();

    charset = Constant.DEFAULT_CHARSET;

    setText("Text");
  }

  @Override
  public void setBytes(byte[] bytes, int size, Charset charset) {
    this.charset = charset;

    textInput.setText(GuiUtils.formatText(new String(bytes, 0, size, charset)));
  }

  @Override
  public byte[] getBytes() {
    return GuiUtils.unformatText(textInput.getText()).getBytes(charset);
  }
}
