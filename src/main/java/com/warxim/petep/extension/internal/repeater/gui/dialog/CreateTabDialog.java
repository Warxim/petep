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
package com.warxim.petep.extension.internal.repeater.gui.dialog;

import com.warxim.petep.core.pdu.SerializedPdu;
import com.warxim.petep.extension.internal.history.model.HistoryFilter;
import com.warxim.petep.extension.internal.repeater.config.RepeaterTabConfig;
import com.warxim.petep.gui.control.pdueditor.PduEditor;
import com.warxim.petep.gui.control.pdueditor.PduEditorConfig;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.gui.dialog.SimpleInputDialog;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.util.PduUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Dialog for creating Repeater Tabs.
 * <p>Displays PDU editor for creating PDU, which will be added to new Repeater tab.</p>
 */
public class CreateTabDialog extends SimpleInputDialog<RepeaterTabConfig> {
    @FXML
    private TextField nameInput;
    @FXML
    private PduEditor pduEditor;

    /**
     * Constructs dialog for tab creation
     * @param extensionHelper Extension helper
     * @param petepHelper PETEP helper for currently running core
     * @throws IOException If the dialog template could not be loaded
     */
    public CreateTabDialog(ExtensionHelper extensionHelper, PetepHelper petepHelper) throws IOException {
        super("/fxml/extension/internal/repeater/CreateTabDialog.fxml", "Create repeater tab", "Create");
        this.pduEditor.init(
                extensionHelper, PduEditorConfig.builder()
                        .automaticLifecycle(false)
                        .strict(false)
                        .build()
        );
        this.pduEditor.load(petepHelper);
    }

    @Override
    protected RepeaterTabConfig obtainResult() {
        var maybePdu = pduEditor.getPdu();
        var name = nameInput.getText();

        if (maybePdu.isEmpty()) {
            return new RepeaterTabConfig(name, SerializedPdu.builder().build(), HistoryFilter.all());
        }
        var pdu = maybePdu.get();

        return new RepeaterTabConfig(
                name,
                PduUtils.serializePdu(pdu),
                HistoryFilter.all()
        );
    }

    @Override
    protected boolean isValid() {
        if (nameInput.getText().length() == 0) {
            Dialogs.createErrorDialog("Name required", "You have to enter name.");
            return false;
        }

        return pduEditor.isValid();
    }
}
