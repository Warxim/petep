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
package com.warxim.petep.extension.internal.common.rulegroup.gui;

import com.warxim.petep.extension.internal.common.rulegroup.Rule;
import com.warxim.petep.extension.internal.common.rulegroup.RuleGroup;
import com.warxim.petep.extension.internal.common.rulegroup.RuleGroupManager;
import com.warxim.petep.gui.dialog.Dialogs;

import java.io.IOException;

/**
 * Dialog for editing of existing rule group.
 * @param <R> Rule type for this dialog
 */
public final class EditRuleGroupDialog<R extends Rule> extends RuleGroupDialog<R> {
    /**
     * RuleGroup that is being edited in this dialog
     */
    private final RuleGroup<R> group;

    /**
     * Edit rule group dialog constructor.
     * @param manager Manager that manages rule groups (contains all rule groups)
     * @param group Group to be edited
     * @throws IOException If the dialog template could not be loaded
     */
    public EditRuleGroupDialog(RuleGroupManager<RuleGroup<R>> manager, RuleGroup<R> group) throws IOException {
        super(manager, "Edit rule group", "Save");
        this.group = group;

        codeInput.setText(group.getCode());
        nameInput.setText(group.getName());
    }

    @Override
    protected boolean isValid() {
        if (!super.isValid()) {
            return false;
        }

        if (!group.getCode().equals(codeInput.getText()) && manager.contains(codeInput.getText())) {
            Dialogs.createErrorDialog("Code used",
                    "The specified code is already used by other rule group.");
            return false;
        }

        return true;
    }

    @Override
    protected RuleGroup<R> obtainResult() {
        if (group.getCode().equals(codeInput.getText())
                && group.getName().equals(nameInput.getText())) {
            return null;
        }

        group.setCode(codeInput.getText());
        group.setName(nameInput.getText());

        return group;
    }
}
