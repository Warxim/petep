/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2021 Michal Válka
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
package com.warxim.petep.extension.internal.udp;

import com.warxim.petep.common.Constant;
import com.warxim.petep.gui.component.ConfigPane;
import com.warxim.petep.gui.dialog.Dialogs;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * UDP configurator GUI.
 */
public class UdpConfigurator extends ConfigPane<UdpConfig> {
    @FXML
    private TextField proxyIpInput;
    @FXML
    private TextField proxyPortInput;
    @FXML
    private TextField targetIpInput;
    @FXML
    private TextField targetPortInput;
    @FXML
    private TextField bufferSizeInput;
    @FXML
    private TextField charsetInput;

    /**
     * Constructs UDP configurator.
     * @throws IOException If the template could not be loaded
     */
    public UdpConfigurator() throws IOException {
        super("/fxml/extension/internal/udp/UdpConfigurator.fxml");

        proxyIpInput.setText("127.0.0.1");
        proxyPortInput.setText("8888");
        bufferSizeInput.setText("16384");
        charsetInput.setText(Constant.DEFAULT_CHARSET.name());
    }

    /**
     * Get TCP configuration from configurator.
     */
    @Override
    public UdpConfig getConfig() {
        return new UdpConfig(
                proxyIpInput.getText(),
                targetIpInput.getText(),
                Integer.parseInt(proxyPortInput.getText()),
                Integer.parseInt(targetPortInput.getText()),
                Integer.parseInt(bufferSizeInput.getText()),
                Charset.forName(charsetInput.getText()));
    }

    /**
     * Sets configuration to configurator.
     */
    @Override
    public void setConfig(UdpConfig config) {
        proxyIpInput.setText(config.getProxyIP());
        targetIpInput.setText(config.getTargetIP());
        proxyPortInput.setText(String.valueOf(config.getProxyPort()));
        targetPortInput.setText(String.valueOf(config.getTargetPort()));
        bufferSizeInput.setText(String.valueOf(config.getBufferSize()));
        charsetInput.setText(config.getCharset().name());
    }

    /**
     * Are the data provided by user valid?
     */
    @Override
    public boolean isValid() {
        if (proxyIpInput.getText().length() == 0) {
            Dialogs.createErrorDialog("Proxy IP required", "You have to enter proxy IP.");
            return false;
        }
        if (proxyPortInput.getText().length() == 0) {
            Dialogs.createErrorDialog("Proxy port required", "You have to enter proxy port.");
            return false;
        }

        if (targetIpInput.getText().length() == 0) {
            Dialogs.createErrorDialog("Target IP required", "You have to enter target IP.");
            return false;
        }

        if (targetPortInput.getText().length() == 0) {
            Dialogs.createErrorDialog("Target port required", "You have to enter target port.");
            return false;
        }

        if (bufferSizeInput.getText().length() == 0) {
            Dialogs.createErrorDialog("Buffer size required", "You have to enter buffer size.");
            return false;
        }

        if (charsetInput.getText().length() == 0) {
            Dialogs.createErrorDialog("Charset required", "You have to enter charset.");
            return false;
        }

        if (!Charset.isSupported(charsetInput.getText())) {
            Dialogs.createErrorDialog("Charset not supported", "You have entered unsupported charset.");
            return false;
        }

        return true;
    }
}
