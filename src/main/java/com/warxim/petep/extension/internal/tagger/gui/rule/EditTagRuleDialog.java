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
package com.warxim.petep.extension.internal.tagger.gui.rule;

import com.warxim.petep.extension.internal.tagger.factory.TagSubruleFactoryManager;
import com.warxim.petep.extension.internal.tagger.rule.TagRule;

import java.io.IOException;

/**
 * Edit tag rule dialog.
 */
public final class EditTagRuleDialog extends TagRuleDialog {
    /**
     * Constructs tag rule dialog for editing.
     * @param factoryManager Manager of tag subrule factories
     * @param rule Rule to be edited
     * @throws IOException If the dialog template could not be loaded
     */
    public EditTagRuleDialog(TagSubruleFactoryManager factoryManager, TagRule rule)
            throws IOException {
        super("Edit tag rule", "Save", factoryManager);

        nameInput.setText(rule.getName());
        descriptionInput.setText(rule.getDescription());
        tagInput.setText(rule.getTag());
        enabledInput.setSelected(rule.isEnabled());
        expressionInput.setText(rule.getExpressionString());

        subrules.addAll(rule.getSubrules());

        if (!generateExpression().equals(rule.getExpressionString())) {
            customExpressionInput.setSelected(true);
        }
    }
}
