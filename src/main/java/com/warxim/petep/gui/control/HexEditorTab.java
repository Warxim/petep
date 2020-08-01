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
import java.util.regex.Pattern;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.util.BytesUtils;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;

@PetepAPI
public class HexEditorTab extends Tab implements BytesEditorTab {
  private static final Pattern HEX_FORMAT_PATTERN_1 = Pattern.compile("[^\\s0-9A-F]");
  private static final Pattern HEX_FORMAT_PATTERN_2 =
      Pattern.compile("([0-9A-F][0-9A-F])([0-9A-F])");
  private static final Pattern HEX_FORMAT_PATTERN_3 =
      Pattern.compile("(^| )([0-9A-F]) ([0-9A-F])($| )");
  private static final Pattern HEX_FORMAT_PATTERN_4 = Pattern.compile("(^| )([0-9A-F])($| )");

  @FXML
  private TextArea textInput;

  public HexEditorTab() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/control/TextEditorTab.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.setClassLoader(getClass().getClassLoader());
    loader.load();

    // Automatic formating.
    textInput.focusedProperty().addListener(this::onTextFocusChange);
    textInput.textProperty().addListener(this::onTextPropertyChange);

    setText("Hex");
  }

  private void onTextFocusChange(
      ObservableValue<? extends Boolean> observable,
      boolean oldValue,
      boolean newValue) {
    if (oldValue) {
      byte[] bytes = BytesUtils
          .stringToBytes(HEX_FORMAT_PATTERN_4.matcher(textInput.getText()).replaceAll("$10$2$3"));

      setBytes(bytes, bytes.length, null);
    }
  }

  private void onTextPropertyChange(
      ObservableValue<? extends String> observable,
      String oldValue,
      String newValue) {
    textInput.setText(HEX_FORMAT_PATTERN_3.matcher(HEX_FORMAT_PATTERN_2
        .matcher(HEX_FORMAT_PATTERN_1.matcher(newValue.toUpperCase()).replaceAll(""))
        .replaceAll("$1 $2")).replaceAll("$1$2$3$4").replace("  ", " "));
  }

  @Override
  public void setBytes(byte[] bytes, int size, Charset charset) {
    textInput.setText(BytesUtils.bytesToString(bytes, size));
  }

  @Override
  public byte[] getBytes() {
    return BytesUtils
        .stringToBytes(HEX_FORMAT_PATTERN_4.matcher(textInput.getText()).replaceAll("$10$2$3"));
  }
}
