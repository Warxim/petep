/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2023 Michal Válka
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
package com.warxim.petep.extension.internal.deluder.proxy;

import com.warxim.petep.common.Constant;
import com.warxim.petep.extension.internal.deluder.DeluderConstant;
import com.warxim.petep.gui.GuiBundle;
import com.warxim.petep.gui.component.ConfigPane;
import com.warxim.petep.gui.dialog.Dialogs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Deluder configurator GUI
 */
public final class DeluderProxyConfigurator extends ConfigPane<DeluderConfig> {
    @FXML
    private Hyperlink website;
    @FXML
    private TextField petepHostInput;
    @FXML
    private TextField petepPortInput;
    @FXML
    private TextField charsetInput;

    /**
     * Constructs Deluder proxy configurator.
     * @throws IOException If the template could not be loaded
     */
    public DeluderProxyConfigurator() throws IOException {
        super("/fxml/extension/internal/deluder/DeluderProxyConfigurator.fxml");

        petepHostInput.setText("127.0.0.1");
        petepPortInput.setText(String.valueOf(DeluderConstant.PETEP_PORT));
        website.setText(DeluderConstant.WEB);
        website.setOnAction(DeluderProxyConfigurator::onWebClick);
        charsetInput.setText(Constant.DEFAULT_CHARSET.name());
    }

    @Override
    public DeluderConfig getConfig() {
        return DeluderConfig.builder()
                .petepHost(petepHostInput.getText())
                .petepPort(Integer.parseInt(petepPortInput.getText()))
                .charset(Charset.forName(charsetInput.getText()))
                .build();
    }

    @Override
    public void setConfig(DeluderConfig config) {
        petepHostInput.setText(config.getPetepHost());
        petepPortInput.setText(String.valueOf(config.getPetepPort()));
        charsetInput.setText(config.getCharset().name());
    }

    @Override
    public boolean isValid() {
        if (petepHostInput.getText().length() == 0) {
            Dialogs.createErrorDialog("PETEP host (IP address) required", "You have to enter PETEP host.");
            return false;
        }
        if (petepPortInput.getText().length() == 0) {
            Dialogs.createErrorDialog("PETEP port required", "You have to enter PETEP port.");
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

    /**
     * Opens Deluder website
     */
    private static void onWebClick(ActionEvent event) {
        try {
            GuiBundle.getInstance().getHostServices().showDocument(DeluderConstant.WEB);
        } catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not open Deluder link.");
        }
    }
}
