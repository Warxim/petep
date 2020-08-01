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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.warxim.petep.extension.internal.common.rule_group.Rule;
import com.warxim.petep.extension.internal.common.rule_group.RuleGroup;
import com.warxim.petep.extension.internal.common.rule_group.RuleGroupManager;
import com.warxim.petep.extension.internal.common.rule_group.intercept.RuleInterceptorModule;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.interceptor.module.InterceptorModule;
import com.warxim.petep.util.GuiUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.util.Pair;

/** Controller or rule groups (contains tabs of rule groups). */
public abstract class RuleGroupsController<R extends Rule> implements Initializable {
  private final ExtensionHelper helper;
  private final RuleGroupManager<RuleGroup<R>> manager;
  private final List<RuleGroupController<R>> controllers;

  private final String title;

  @FXML
  private TabPane tabs;
  @FXML
  private Label titleLabel;

  /** Rule groups controller constrcutor. */
  public RuleGroupsController(
      String title,
      RuleGroupManager<RuleGroup<R>> groupManager,
      ExtensionHelper extensionHelper) {
    this.title = title;
    helper = extensionHelper;
    manager = groupManager;
    controllers = new ArrayList<>();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    for (RuleGroup<R> group : manager.getMap().values()) {
      addGroupTab(group);
    }

    titleLabel.setText(title);
  }

  /** Adds new tab for rule group to the tab pane. */
  private final void addGroupTab(RuleGroup<R> group) {
    try {
      Pair<Node, RuleGroupController<R>> pair = createGroupTabNode(group);

      controllers.add(pair.getValue());

      GuiUtils.addTabToTabPane(tabs, group.getName(), pair.getKey());
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Could not load group tab!", e);
    }
  }

  @FXML
  private final void onNewButtonClick(ActionEvent event) {
    try {
      NewRuleGroupDialog<R> dialog = new NewRuleGroupDialog<>(manager);

      Optional<RuleGroup<R>> data = dialog.showAndWait();

      if (!data.isPresent() || data.get() == null) {
        return;
      }

      manager.add(data.get());

      addGroupTab(data.get());
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Exception during openning of group dialog", e);
    }
  }

  @FXML
  private final void onEditButtonClick(ActionEvent event) {
    RuleGroup<R> group =
        controllers.get(tabs.getSelectionModel().getSelectedIndex()).getRuleGroup();

    if (group == null) {
      return;
    }

    String oldCode = group.getCode();

    try {
      EditRuleGroupDialog<R> dialog = new EditRuleGroupDialog<>(manager, group);

      Optional<RuleGroup<R>> data = dialog.showAndWait();

      if (!data.isPresent() || data.get() == null) {
        return;
      }

      manager.remove(oldCode);
      manager.add(data.get());

      tabs.getSelectionModel().getSelectedItem().setText(data.get().getName());
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Exception during openning of rule group dialog", e);
    }
  }

  @FXML
  private final void onRemoveButtonClick(ActionEvent event) {
    int index = tabs.getSelectionModel().getSelectedIndex();

    RuleGroup<R> group = controllers.get(index).getRuleGroup();

    if (group == null) {
      return;
    }

    if (isGroupUsed(group)) {
      Dialogs.createErrorDialog("Rule group is used",
          "Rule group is used by interceptors and cannot be deleted. Please, remove interceptors that use this group first.");
      return;
    }

    // Does user really want to remove group?
    if (!Dialogs.createYesOrNoDialog("Are you sure?",
        "Do you really want to remove group '" + group.getName() + "'?")) {
      return;
    }

    // Remove group
    manager.remove(group.getCode());

    // Remove controller
    controllers.remove(index);

    // Remove tab
    tabs.getTabs().remove(index);
  }

  /** Check whether the group is used by any interceptor modules. */
  private final boolean isGroupUsed(RuleGroup<R> group) {
    Class<? extends RuleInterceptorModule<R>> moduleClass = getInterceptorModuleClass();

    // Check modules in direction C2S (Client -> Server)
    for (InterceptorModule module : helper.getInterceptorModulesC2S()) {
      if (!(moduleClass.isInstance(module))) {
        continue;
      }

      if (moduleClass.cast(module).getRuleGroup() == group) {
        return true;
      }
    }

    // Check modules in direction S2C (Client <- Server)
    for (InterceptorModule module : helper.getInterceptorModulesS2C()) {
      if (!(moduleClass.isInstance(module))) {
        continue;
      }

      if (moduleClass.cast(module).getRuleGroup() == group) {
        return true;
      }
    }

    return false;
  }

  /** Creates group tab node for rule group. */
  protected abstract Pair<Node, RuleGroupController<R>> createGroupTabNode(RuleGroup<R> group)
      throws IOException;

  /** Returns interceptor module class. */
  protected abstract Class<? extends RuleInterceptorModule<R>> getInterceptorModuleClass();
}
