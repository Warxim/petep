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
package com.warxim.petep.extension.internal.scripter.gui.rule;

import com.warxim.petep.extension.internal.scripter.helper.ScriptHelperFactory;
import com.warxim.petep.extension.internal.scripter.rule.FileScript;
import com.warxim.petep.extension.internal.scripter.rule.Script;
import com.warxim.petep.extension.internal.scripter.rule.ScriptType;
import com.warxim.petep.extension.internal.scripter.rule.StringScript;

import java.io.IOException;

/**
 * Dialog for editing script.
 */
public final class EditScriptDialog extends ScriptDialog {
    /**
     * Constructs edit script dialog.
     * @param factory Factory for creation of script helpers
     * @param script Script to be edited
     * @throws IOException If the dialog template could not be loaded
     */
    public EditScriptDialog(ScriptHelperFactory factory, Script script) throws IOException {
        super("Edit script", "Save", factory);

        nameInput.setText(script.getName());
        descriptionInput.setText(script.getDescription());
        languageInput.setText(script.getLanguage());
        enabledInput.setSelected(script.isEnabled());

        typeInput.getSelectionModel().select(script.getType());
        if (script.getType() == ScriptType.STRING) {
            scriptInput.setText(((StringScript) script).getScript());
            onTypeChange(null);
            return;
        }

        pathInput.setText(((FileScript) script).getPath());
        onTypeChange(null);
    }
}
