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
package com.warxim.petep.extension.internal.tagger.factory.internal.size;

import com.warxim.petep.extension.internal.tagger.factory.TagSubruleConfigurator;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleData;
import com.warxim.petep.gui.dialog.Dialogs;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Configurator for configuring "size" subrule data.
 */
public final class SizeSubruleConfigurator extends TagSubruleConfigurator {
    @FXML
    private TextField sizeInput;

    /**
     * Constructs tag subrule configurator for Size subrule.
     * @throws IOException If the template could not be loaded
     */
    public SizeSubruleConfigurator() throws IOException {
        super("/fxml/extension/internal/tagger/factory/SizeSubrule.fxml");
    }

    @Override
    public TagSubruleData getConfig() {
        return new SizeData(Integer.parseInt(sizeInput.getText()));
    }

    @Override
    public void setConfig(TagSubruleData config) {
        sizeInput.setText(String.valueOf(((SizeData) config).getSize()));
    }

    @Override
    public boolean isValid() {
        try {
            int size = Integer.parseInt(sizeInput.getText());
            if (size < 0) {
                Dialogs.createErrorDialog("Invalid size", "Size has to be number greater or equal to 0.");
                return false;
            }
        } catch (NumberFormatException e) {
            Dialogs.createErrorDialog("Invalid size", "Size has to be number greater or equal to 0.");
            return false;
        }

        return true;
    }
}
