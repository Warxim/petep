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
package com.warxim.petep.extension.internal.tagger.factory.internal.contains;

import com.warxim.petep.extension.internal.tagger.factory.TagSubruleConfigurator;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleData;
import com.warxim.petep.gui.control.byteseditor.BytesEditor;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.util.GuiUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Configurator for configuring "contains" subrule data.
 */
public final class ContainsSubruleConfigurator extends TagSubruleConfigurator {
    @FXML
    private BytesEditor dataInput;
    @FXML
    private TextField indexInput;

    /**
     * Constructs tag subrule configurator for Contains subrule.
     * @throws IOException If the template could not be loaded
     */
    public ContainsSubruleConfigurator() throws IOException {
        super("/fxml/extension/internal/tagger/factory/ContainsSubrule.fxml");

        indexInput.setTooltip(GuiUtils.createTooltip(
                "Use -1 to look for the bytes at any index, or use specific index (zero-based numbering)."
        ));
    }

    @Override
    public TagSubruleData getConfig() {
        return new ContainsData(
                dataInput.getBytes(),
                dataInput.getCharset(),
                Integer.parseInt(indexInput.getText()));
    }

    @Override
    public void setConfig(TagSubruleData config) {
        dataInput.setData(
                ((ContainsData) config).getData(),
                ((ContainsData) config).getCharset());
        indexInput.setText(String.valueOf(((ContainsData) config).getIndex()));
    }

    @Override
    public boolean isValid() {
        if (dataInput.getBytes().length == 0) {
            Dialogs.createErrorDialog("Data required", "You have enter data.");
            return false;
        }

        try {
            int index = Integer.parseInt(indexInput.getText());
            if (index < 0 && index != -1) {
                Dialogs.createErrorDialog("Invalid index",
                        "Index has to be -1 or number greater or equal to 0.");
                return false;
            }
        } catch (NumberFormatException e) {
            Dialogs.createErrorDialog("Invalid index",
                    "Index has to be -1 or number greater or equal to 0.");
            return false;
        }

        return true;
    }
}
