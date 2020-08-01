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
import com.warxim.petep.extension.internal.common.rule_group.config.RuleInterceptorConfig;
import com.warxim.petep.gui.component.ConfigPane;
import com.warxim.petep.gui.dialog.Dialogs;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

/** Rule interceptor configurator. */
public final class RuleInterceptorConfigurator<R extends Rule>
    extends ConfigPane<RuleInterceptorConfig> {
  @FXML
  private ComboBox<RuleGroup<R>> groupInput;

  private final RuleGroupManager<RuleGroup<R>> manager;

  /** Rule interceptor configurator constructor. */
  public RuleInterceptorConfigurator(RuleGroupManager<RuleGroup<R>> manager) throws IOException {
    super("/fxml/extension/internal/common/rule_group/RuleInterceptorConfigurator.fxml");
    this.manager = manager;

    groupInput.getItems().setAll(manager.getList());

    groupInput.setConverter(new StringConverter<RuleGroup<R>>() {
      @Override
      public String toString(RuleGroup<R> group) {
        return group == null ? "" : group.getName();
      }

      @Override
      public RuleGroup<R> fromString(String str) {
        return null;
      }
    });

    groupInput.getSelectionModel().selectLast();
  }

  @Override
  public RuleInterceptorConfig getConfig() {
    return new RuleInterceptorConfig(groupInput.getSelectionModel().getSelectedItem().getCode());
  }

  @Override
  public void setConfig(RuleInterceptorConfig config) {
    groupInput.getSelectionModel().select(manager.get(config.getRuleGroupCode()));
  }

  @Override
  public boolean isValid() {
    if (groupInput.getSelectionModel().getSelectedItem() == null) {
      Dialogs.createErrorDialog("Replace group required", "You have to select replace group.");
      return false;
    }

    return true;
  }
}
