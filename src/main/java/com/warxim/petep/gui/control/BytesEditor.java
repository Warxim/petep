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
import java.util.Optional;
import com.warxim.petep.common.Constant;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.gui.dialog.Dialogs;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;

/** Byte array editor. */
@PetepAPI
public class BytesEditor extends AnchorPane {
  protected Charset charset;
  protected byte[] bytes;

  @FXML
  protected TabPane tabs;

  @FXML
  protected Label charsetLabel;

  public BytesEditor() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/control/BytesEditor.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.setClassLoader(getClass().getClassLoader());
    loader.load();

    charset = Constant.DEFAULT_CHARSET;

    charsetLabel.setText(charset.toString());

    tabs.getSelectionModel().selectedItemProperty().addListener(this::onTabChange);

    tabs.getTabs().add(new TextEditorTab());
    tabs.getTabs().add(new HexEditorTab());
  }

  protected void onTabChange(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
    if (oldTab != null) {
      bytes = ((BytesEditorTab) oldTab).getBytes();
    }

    if (bytes == null) {
      return;
    }

    if (newTab != null) {
      ((BytesEditorTab) newTab).setBytes(bytes, bytes.length, charset);
    }
  }

  public void setData(byte[] bytes, int size, Charset charset) {
    this.bytes = bytes;
    this.charset = charset;

    BytesEditorTab currentTab = (BytesEditorTab) tabs.getSelectionModel().getSelectedItem();
    if (currentTab != null) {
      currentTab.setBytes(bytes, size, charset);
    }

    charsetLabel.setText(charset.displayName());
  }

  public void setData(byte[] bytes, Charset charset) {
    setData(bytes, bytes.length, charset);
  }

  public void setBytes(byte[] bytes, int size) {
    this.bytes = bytes;

    BytesEditorTab currentTab = (BytesEditorTab) tabs.getSelectionModel().getSelectedItem();
    if (currentTab != null) {
      currentTab.setBytes(bytes, size, charset);
    }
  }

  public void setBytes(byte[] bytes) {
    setBytes(bytes, bytes.length);
  }

  public void setCharset(Charset charset) {
    BytesEditorTab currentTab = (BytesEditorTab) tabs.getSelectionModel().getSelectedItem();
    if (currentTab != null) {
      bytes = currentTab.getBytes();
    }

    this.charset = charset;

    if (currentTab != null) {
      currentTab.setBytes(bytes, bytes.length, charset);
    }

    charsetLabel.setText(charset.displayName());
  }

  public byte[] getBytes() {
    bytes = ((BytesEditorTab) tabs.getSelectionModel().getSelectedItem()).getBytes();

    return bytes;
  }

  public Charset getCharset() {
    return charset;
  }

  @FXML
  protected void onCharsetClick() {
    TextInputDialog dialog = new TextInputDialog(charset.toString());
    dialog.setTitle("Change charset");
    dialog.setHeaderText("Change charset");
    dialog.setContentText("New charset:");

    Optional<String> result = dialog.showAndWait();

    result.ifPresent((String newCharset) -> {
      if (!Charset.isSupported(newCharset)) {
        Dialogs.createErrorDialog("Charset not supported", "Specified charset is not supported!");
        return;
      }

      setCharset(Charset.forName(newCharset));
    });
  }
}
