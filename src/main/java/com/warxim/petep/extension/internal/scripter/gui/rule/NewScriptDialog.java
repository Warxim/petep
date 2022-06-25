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
import com.warxim.petep.extension.internal.scripter.rule.ScriptType;

import java.io.IOException;

/**
 * Dialog for creating new script.
 */
public final class NewScriptDialog extends ScriptDialog {
    /**
     * Constructs new script dialog.
     * @param factory Factory for creation of script helpers
     * @throws IOException If the dialog template could not be loaded
     */
    public NewScriptDialog(ScriptHelperFactory factory) throws IOException {
        super("New script", "Create", factory);
        typeInput.getSelectionModel().select(ScriptType.STRING);
        enabledInput.setSelected(true);
        onTypeChange(null);
    }
}
