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
import com.warxim.petep.extension.internal.common.rulegroup.intercept.RuleInterceptorModule;
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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller or rule groups (contains tabs of rule groups).
 * @param <R> Rule type for this controller
 */
public abstract class RuleGroupsController<R extends Rule> implements Initializable {
    /**
     * Extension helper for checking that the group is not used, when removing it.
     */
    private final ExtensionHelper extensionHelper;
    /**
     * Rule manager that handles all rule groups.
     */
    private final RuleGroupManager<RuleGroup<R>> manager;
    /**
     * List of rule group controllers (one controller per rule group)
     */
    private final List<RuleGroupController<R>> controllers;
    /**
     * Title of the application tab
     */
    private final String title;

    @FXML
    private TabPane tabs;
    @FXML
    private Label titleLabel;

    /**
     * Constructs rule groups controller.
     * @param title Title of the controller
     * @param groupManager Manager for working with groups of rules
     * @param extensionHelper Helper for extensions (for accessing configured modules]
     */
    protected RuleGroupsController(
            String title,
            RuleGroupManager<RuleGroup<R>> groupManager,
            ExtensionHelper extensionHelper) {
        this.title = title;
        this.extensionHelper = extensionHelper;
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

    /**
     * Creates group tab node for rule group.
     */
    protected abstract Pair<Node, RuleGroupController<R>> createGroupTabNode(RuleGroup<R> group) throws IOException;

    /**
     * Returns interceptor module class.
     */
    protected abstract Class<? extends RuleInterceptorModule<R>> getInterceptorModuleClass();

    /**
     * Opens NewRuleGroupDialog for creating new group and adds it to the manager.
     */
    @FXML
    protected void onNewButtonClick(ActionEvent event) {
        try {
            var dialog = new NewRuleGroupDialog<>(manager);
            var data = dialog.showAndWait();

            if (data.isEmpty()) {
                return;
            }

            manager.add(data.get());

            addGroupTab(data.get());
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Exception during openning of group dialog", e);
        }
    }

    /**
     * Opens EditRuleGroupDialog for editing existing selected group.
     */
    @FXML
    protected void onEditButtonClick(ActionEvent event) {
        int index = tabs.getSelectionModel().getSelectedIndex();
        var group = controllers.get(index).getRuleGroup();

        if (group == null) {
            return;
        }

        var oldCode = group.getCode();

        try {
            var dialog = new EditRuleGroupDialog<>(manager, group);
            var data = dialog.showAndWait();

            if (data.isEmpty()) {
                return;
            }

            manager.remove(oldCode);
            manager.add(data.get());

            tabs.getSelectionModel().getSelectedItem().setText(data.get().getName());
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Exception during openning of rule group dialog", e);
        }
    }

    /**
     * Removes selected group.
     */
    @FXML
    protected void onRemoveButtonClick(ActionEvent event) {
        int index = tabs.getSelectionModel().getSelectedIndex();
        var group = controllers.get(index).getRuleGroup();

        if (group == null) {
            return;
        }

        if (isGroupUsed(group)) {
            Dialogs.createErrorDialog(
                    "Rule group is used",
                    "Rule group is used by interceptors and cannot be deleted. Please, remove interceptors that use this group first.");
            return;
        }

        // Does user really want to remove group?
        if (!Dialogs.createYesOrNoDialog(
                "Are you sure?",
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

    /**
     * Check whether the group is used by any interceptor modules.
     */
    private boolean isGroupUsed(RuleGroup<R> group) {
        var moduleClass = getInterceptorModuleClass();

        // Check modules in direction C2S (Client -> Server)
        for (var module : extensionHelper.getInterceptorModulesC2S()) {
            if (!(moduleClass.isInstance(module))) {
                continue;
            }

            if (moduleClass.cast(module).getRuleGroup() == group) {
                return true;
            }
        }

        // Check modules in direction S2C (Client <- Server)
        for (var module : extensionHelper.getInterceptorModulesS2C()) {
            if (!(moduleClass.isInstance(module))) {
                continue;
            }

            if (moduleClass.cast(module).getRuleGroup() == group) {
                return true;
            }
        }

        return false;
    }

    /**
     * Adds new tab for rule group to the tab pane.
     */
    private void addGroupTab(RuleGroup<R> group) {
        try {
            var pair = createGroupTabNode(group);

            controllers.add(pair.getValue());

            GuiUtils.addTabToTabPane(tabs, group.getName(), pair.getKey());
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not load group tab!", e);
        }
    }
}
