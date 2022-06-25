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
package com.warxim.petep.extension.internal.http.modifier.replaceheader;

import com.warxim.petep.extension.internal.http.pdu.HttpUtils;
import com.warxim.petep.extension.internal.modifier.factory.ModifierConfigurator;
import com.warxim.petep.extension.internal.modifier.factory.ModifierData;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.util.GuiUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Configurator for configuring Replace Header modifiers.
 */
public final class ReplaceHeaderModifierConfigurator extends ModifierConfigurator {
    @FXML
    private TextField nameInput;
    @FXML
    private TextArea whatInput;
    @FXML
    private TextArea withInput;

    /**
     * Constructs modifier configurator for Replace Header rule
     * @throws IOException If the template could not be loaded
     */
    public ReplaceHeaderModifierConfigurator() throws IOException {
        super("/fxml/extension/internal/http/modifier/ReplaceHeader.fxml");

        nameInput.setTooltip(GuiUtils.createTooltip(
                "Leave the header name empty if you want to perform the replacement across all headers."
        ));
    }

    @Override
    public ModifierData getConfig() {
        return new ReplaceHeaderData(HttpUtils.formatHeaderName(nameInput.getText()), whatInput.getText(), withInput.getText());
    }

    @Override
    public void setConfig(ModifierData config) {
        var data = ((ReplaceHeaderData) config);
        nameInput.setText(data.getHeader());
        whatInput.setText(data.getWhat());
        withInput.setText(data.getWith());
    }

    @Override
    public boolean isValid() {
        if (nameInput.getText().length() == 0) {
            Dialogs.createErrorDialog("Header required", "You have to enter header to remove.");
            return false;
        }

        if (whatInput.getText().length() == 0) {
            Dialogs.createErrorDialog("What required", "You have to enter what you want to replace.");
            return false;
        }

        return true;
    }
}
