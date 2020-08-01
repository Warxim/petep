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
package com.warxim.petep.extension.internal.modifier.gui;

import java.io.IOException;
import com.warxim.petep.extension.internal.common.rule_group.RuleGroup;
import com.warxim.petep.extension.internal.common.rule_group.gui.RuleGroupController;
import com.warxim.petep.extension.internal.common.rule_group.gui.RuleGroupsController;
import com.warxim.petep.extension.internal.common.rule_group.intercept.RuleInterceptorModule;
import com.warxim.petep.extension.internal.modifier.factory.ModifierFactoryManager;
import com.warxim.petep.extension.internal.modifier.intercept.ModifierInterceptorModule;
import com.warxim.petep.extension.internal.modifier.rule.ModifyRule;
import com.warxim.petep.extension.internal.modifier.rule.ModifyRuleGroupManager;
import com.warxim.petep.helper.ExtensionHelper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.util.Pair;

public final class ModifierController extends RuleGroupsController<ModifyRule> {
  private final ModifierFactoryManager factoryManager;

  public ModifierController(
      ModifyRuleGroupManager groupManager,
      ExtensionHelper extensionHelper,
      ModifierFactoryManager factoryManager) {
    super("Modifier", groupManager, extensionHelper);
    this.factoryManager = factoryManager;
  }

  @Override
  protected Class<? extends RuleInterceptorModule<ModifyRule>> getInterceptorModuleClass() {
    return ModifierInterceptorModule.class;
  }

  @Override
  protected Pair<Node, RuleGroupController<ModifyRule>> createGroupTabNode(
      RuleGroup<ModifyRule> group) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(
        getClass().getResource("/fxml/extension/internal/modifier/ModifyRuleGroup.fxml"));

    ModifyGroupController controller = new ModifyGroupController(group, factoryManager);

    fxmlLoader.setController(controller);

    return new Pair<>(fxmlLoader.load(), controller);
  }
}
