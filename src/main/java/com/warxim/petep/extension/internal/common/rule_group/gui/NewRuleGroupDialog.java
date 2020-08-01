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
package com.warxim.petep.extension.internal.common.rule_group.gui;

import java.io.IOException;
import com.warxim.petep.extension.internal.common.rule_group.Rule;
import com.warxim.petep.extension.internal.common.rule_group.RuleGroup;
import com.warxim.petep.extension.internal.common.rule_group.RuleGroupManager;
import com.warxim.petep.gui.dialog.Dialogs;

/** Dialog for creating new rule group. */
public final class NewRuleGroupDialog<R extends Rule> extends RuleGroupDialog<R> {
  /** New rule group dialog constructor. */
  public NewRuleGroupDialog(RuleGroupManager<RuleGroup<R>> manager) throws IOException {
    super(manager, "New rule group", "Create");
  }

  @Override
  protected boolean isValid() {
    if (!super.isValid()) {
      return false;
    }

    if (manager.contains(codeInput.getText())) {
      Dialogs.createErrorDialog("Code used",
          "The specified code is already used by other rule group.");
      return false;
    }

    return true;
  }

  @Override
  protected RuleGroup<R> obtainResult() {
    return new RuleGroup<>(codeInput.getText(), nameInput.getText());
  }
}
