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
package com.warxim.petep.extension.internal.test;

import java.io.IOException;
import com.warxim.petep.gui.component.ConfigPane;
import com.warxim.petep.gui.control.BytesEditor;
import com.warxim.petep.gui.dialog.Dialogs;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public final class TestProxyConfigurator extends ConfigPane<TestProxyConfig> {

  @FXML
  private BytesEditor sendInput;

  @FXML
  private BytesEditor receiveInput;

  @FXML
  private TextField connectionsInput;

  @FXML
  private TextField delayInput;

  public TestProxyConfigurator() throws IOException {
    super("/fxml/extension/internal/test/TestProxyConfigurator.fxml");
  }

  @Override
  public TestProxyConfig getConfig() {
    return new TestProxyConfig(sendInput.getBytes(), receiveInput.getBytes(),
        Integer.parseInt(connectionsInput.getText()), Integer.parseInt(delayInput.getText()));
  }

  @Override
  public void setConfig(TestProxyConfig config) {
    sendInput.setBytes(config.getBytesToSend());
    receiveInput.setBytes(config.getBytesToReceive());
    connectionsInput.setText(String.valueOf(config.getNumberOfConnections()));
    delayInput.setText(String.valueOf(config.getSendDelay()));
  }

  @Override
  public boolean isValid() {
    if (sendInput.getBytes().length == 0) {
      Dialogs.createErrorDialog("Send data required", "You have to enter send data.");
      return false;
    }

    if (receiveInput.getBytes().length == 0) {
      Dialogs.createErrorDialog("Reive data required", "You have to enter receive data.");
      return false;
    }

    try {
      if (Integer.parseInt(connectionsInput.getText()) <= 0) {
        Dialogs.createErrorDialog("Invalid number of connections",
            "Number of connections has to be number > 0.");
      }
    } catch (NumberFormatException e) {
      Dialogs.createErrorDialog("Invalid number of connections",
          "Number of connections has to be number > 0.");
      return false;
    }

    try {
      if (Integer.parseInt(delayInput.getText()) < 0) {
        Dialogs.createErrorDialog("Invalid send delay", "Send delay has to be number >= 0.");
      }
    } catch (NumberFormatException e) {
      Dialogs.createErrorDialog("Invalid send delay", "Send delay has to be number >= 0.");
      return false;
    }

    return true;
  }
}
