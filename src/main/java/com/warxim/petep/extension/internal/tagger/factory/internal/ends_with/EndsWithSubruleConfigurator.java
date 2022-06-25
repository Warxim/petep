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
package com.warxim.petep.extension.internal.tagger.factory.internal.ends_with;

import com.warxim.petep.extension.internal.tagger.factory.TagSubruleConfigurator;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleData;
import com.warxim.petep.gui.control.BytesEditor;
import com.warxim.petep.gui.dialog.Dialogs;
import javafx.fxml.FXML;

import java.io.IOException;

/**
 * Configurator for configuring "endsWith" subrule data.
 */
public final class EndsWithSubruleConfigurator extends TagSubruleConfigurator {
    @FXML
    private BytesEditor dataInput;

    /**
     * Constructs tag subrule configurator for EndsWith subrule.
     * @throws IOException If the template could not be loaded
     */
    public EndsWithSubruleConfigurator() throws IOException {
        super("/fxml/extension/internal/tagger/factory/EndsWithSubrule.fxml");
    }

    @Override
    public TagSubruleData getConfig() {
        return new EndsWithData(dataInput.getBytes(), dataInput.getCharset());
    }

    @Override
    public void setConfig(TagSubruleData config) {
        var data = (EndsWithData) config;
        dataInput.setData(
                data.getData(),
                data.getCharset());
    }

    @Override
    public boolean isValid() {
        if (dataInput.getBytes().length == 0) {
            Dialogs.createErrorDialog("Data required", "You have enter data.");
            return false;
        }

        return true;
    }
}
