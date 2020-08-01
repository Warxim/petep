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
import com.warxim.petep.gui.dialog.SimpleInputDialog;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

/** Rule group dialog. */
public abstract class RuleGroupDialog<R extends Rule> extends SimpleInputDialog<RuleGroup<R>> {
  @FXML
  protected TextField nameInput;
  @FXML
  protected TextField codeInput;

  protected final RuleGroupManager<RuleGroup<R>> manager;

  /** Rule group dialog constructor. */
  public RuleGroupDialog(RuleGroupManager<RuleGroup<R>> manager, String title, String okText)
      throws IOException {
    super("/fxml/extension/internal/common/rule_group/RuleGroupDialog.fxml", title, okText);
    this.manager = manager;
  }

  @Override
  protected boolean isValid() {
    if (nameInput.getText().length() == 0) {
      Dialogs.createErrorDialog("Name required", "You have to enter name.");
      return false;
    }

    if (codeInput.getText().length() == 0) {
      Dialogs.createErrorDialog("Code required", "You have to enter code.");
      return false;
    }

    return true;
  }
}
