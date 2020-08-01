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
package com.warxim.petep.extension.internal.external_http_proxy;

import java.net.URL;
import java.util.ResourceBundle;
import com.warxim.petep.gui.dialog.Dialogs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

/** External HTTP Proxy settings controller. */
public final class EHTTPPSettingsController implements Initializable {
  private final EHTTPPConfig config;

  @FXML
  private TextField serverIp;
  @FXML
  private TextField serverPort;
  @FXML
  private TextField proxyIp;
  @FXML
  private TextField proxyPort;

  public EHTTPPSettingsController(EHTTPPConfig config) {
    this.config = config;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    serverIp.setText(config.getServerIp());
    serverPort.setText(String.valueOf(config.getServerPort()));
    proxyIp.setText(config.getProxyIp());
    proxyPort.setText(String.valueOf(config.getProxyPort()));
  }

  @FXML
  private void onSaveButtonClick(ActionEvent event) {
    String sIp = serverIp.getText();
    if (sIp.length() == 0) {
      Dialogs.createErrorDialog("Server IP required", "You have to enter server IP.");
      return;
    }

    int sPort;
    try {
      sPort = Integer.parseInt(serverPort.getText());
      if (sPort <= 0 || sPort > 65535) {
        Dialogs.createErrorDialog("Invalid server port",
            "Server port has to be number (1 - 65535).");
      }
    } catch (NumberFormatException e) {
      Dialogs.createErrorDialog("Invalid server port", "Server port has to be number.");
      return;
    }

    String pIp = proxyIp.getText();
    if (pIp.length() == 0) {
      Dialogs.createErrorDialog("Proxy IP required", "You have to enter proxy IP.");
      return;
    }

    int pPort;
    try {
      pPort = Integer.parseInt(proxyPort.getText());
      if (pPort <= 0 || pPort > 65535) {
        Dialogs.createErrorDialog("Invalid proxy port", "Proxy port has to be number (1 - 65535).");
      }
    } catch (NumberFormatException e) {
      Dialogs.createErrorDialog("Invalid proxy port", "Proxy port has to be number.");
      return;
    }

    config.set(sIp, sPort, pIp, pPort);

    Dialogs.createInfoDialog("HTTP Proxy saved", "HTTP Proxy has been saved!");
  }
}
