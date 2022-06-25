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
package com.warxim.petep.extension.internal.http.tagger.headercontains;

import com.warxim.petep.extension.internal.tagger.factory.TagSubruleConfigurator;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleData;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.util.GuiUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Configurator for configuring "HeaderContains" tag subrule.
 */
public final class HeaderContainsSubruleConfigurator extends TagSubruleConfigurator {
    @FXML
    private TextField nameInput;
    @FXML
    private TextField valueInput;

    /**
     * Constructs tag subrule configurator for Header Contains subrule.
     * @throws IOException If the template could not be loaded
     */
    public HeaderContainsSubruleConfigurator() throws IOException {
        super("/fxml/extension/internal/http/tagger/HeaderContains.fxml");

        nameInput.setTooltip(GuiUtils.createTooltip("Leave this field empty if you want to check all headers."));
    }

    @Override
    public TagSubruleData getConfig() {
        return new HeaderContainsData(nameInput.getText(), valueInput.getText());
    }

    @Override
    public void setConfig(TagSubruleData config) {
        nameInput.setText(((HeaderContainsData) config).getHeader());
        valueInput.setText(((HeaderContainsData) config).getValue());
    }

    @Override
    public boolean isValid() {
        if (valueInput.getText().length() == 0) {
            Dialogs.createErrorDialog("Header value required", "You have to enter header value.");
            return false;
        }

        return true;
    }
}
