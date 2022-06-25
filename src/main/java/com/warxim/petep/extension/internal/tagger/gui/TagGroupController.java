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
package com.warxim.petep.extension.internal.tagger.gui;

import com.warxim.booleanexpressioninterpreter.InvalidExpressionException;
import com.warxim.petep.extension.internal.common.rulegroup.RuleGroup;
import com.warxim.petep.extension.internal.common.rulegroup.gui.RuleGroupController;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleFactoryManager;
import com.warxim.petep.extension.internal.tagger.gui.rule.EditTagRuleDialog;
import com.warxim.petep.extension.internal.tagger.gui.rule.NewTagRuleDialog;
import com.warxim.petep.extension.internal.tagger.rule.TagRule;
import com.warxim.petep.gui.dialog.Dialogs;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tag group controller.
 */
public final class TagGroupController extends RuleGroupController<TagRule> implements Initializable {
    private final TagSubruleFactoryManager factoryManager;

    @FXML
    private TableView<TagRule> table;
    @FXML
    private TableColumn<TagRule, String> nameColumn;
    @FXML
    private TableColumn<TagRule, String> tagColumn;
    @FXML
    private TableColumn<TagRule, Boolean> enabledColumn;

    /**
     * Tag group controller constructor.
     * @param group Group of rules to show in the controller
     * @param factoryManager Manager of factories for obtaining factories for rule creation
     */
    public TagGroupController(RuleGroup<TagRule> group, TagSubruleFactoryManager factoryManager) {
        super(group);
        this.factoryManager = factoryManager;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        tagColumn.setCellValueFactory(new PropertyValueFactory<>("tag"));
        enabledColumn.setCellValueFactory(new PropertyValueFactory<>("enabled"));

        table.setItems(FXCollections.observableList(group.getRules()));

        table.setOnMousePressed(this::onMouseClick);

        initContextMenu();
    }

    /**
     * Shows dialog for creating new tag rule and adds the created rule to group.
     */
    @FXML
    private void onNewButtonClick(ActionEvent event) {
        try {
            var dialog = new NewTagRuleDialog(factoryManager);
            var maybeRule = dialog.showAndWait();
            if (maybeRule.isEmpty()) {
                return;
            }

            group.addRule(maybeRule.get());

            refresh();
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Exception during openning of tag rule dialog", e);
        }
    }

    /**
     * Shows dialog for editing existing tag rule.
     */
    @FXML
    private void onEditButtonClick(ActionEvent event) {
        var rule = table.getSelectionModel().getSelectedItem();
        if (rule == null) {
            return;
        }

        try {
            var dialog = new EditTagRuleDialog(factoryManager, rule);
            var maybeRule = dialog.showAndWait();

            if (maybeRule.isEmpty()) {
                return;
            }

            group.replace(rule, maybeRule.get());

            refresh();
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Exception during openning of tag rule dialog", e);
        }
    }

    /**
     * Removes selected rule from group.
     */
    @FXML
    private void onRemoveButtonClick(ActionEvent event) {
        var rule = table.getSelectionModel().getSelectedItem();
        if (rule == null) {
            return;
        }

        // Does user really want to remove rule?
        if (!Dialogs.createYesOrNoDialog("Are you sure?",
                "Do you really want to remove rule '" + rule.getName() + "'?")) {
            return;
        }

        group.removeRule(rule);

        refresh();
    }

    /**
     * Moves modifier rule group up in the modifier rule group (to the right).
     */
    @FXML
    private void onMoveUpButtonClick(ActionEvent event) {
        int index = table.getSelectionModel().getSelectedIndex();
        if (index <= 0) {
            return;
        }

        group.swapRules(index, index - 1);

        refresh();
    }

    /**
     * Moves modifier rule group down in the modifier rule group (to the left).
     */
    @FXML
    private void onMoveDownButtonClick(ActionEvent event) {
        int index = table.getSelectionModel().getSelectedIndex();
        if (index == -1 || index == group.size() - 1) {
            return;
        }

        group.swapRules(index, index + 1);

        refresh();
    }

    /**
     * Initializes context menu of table for actions on rules.
     */
    private void initContextMenu() {
        var contextMenu = new ContextMenu();

        var editItem = new MenuItem("Edit");
        editItem.setOnAction(this::onEditButtonClick);

        var enableDisableItem = new MenuItem("Enable/Disable");
        enableDisableItem.setOnAction(this::onEnableDisableClick);

        var duplicateItem = new MenuItem("Duplicate");
        duplicateItem.setOnAction(this::onDuplicateButtonClick);

        var removeItem = new MenuItem("Remove");
        removeItem.setOnAction(this::onRemoveButtonClick);

        contextMenu.getItems().addAll(editItem, enableDisableItem, duplicateItem, removeItem);

        table.setContextMenu(contextMenu);
    }

    /**
     * Handles duplicate context menu item. (Duplicates selected rule.)
     */
    private void onDuplicateButtonClick(ActionEvent event) {
        var ruleIndex = table.getSelectionModel().getSelectedIndex();
        if (ruleIndex == -1) {
            return;
        }

        var rule = table.getItems().get(ruleIndex);

        var newRule = rule.copy();
        group.addRule(ruleIndex + 1, newRule);
        refresh();
    }

    /**
     * Handles enable/disable context menu item. (Enables/disables selected rule.)
     */
    private void onEnableDisableClick(ActionEvent event) {
        var rule = table.getSelectionModel().getSelectedItem();
        if (rule == null) {
            return;
        }

        try {
            var newRule = new TagRule(rule.getName(), rule.getDescription(), !rule.isEnabled(), rule.getTag(), rule.getSubrules(), rule.getExpressionString());
            group.replace(rule, newRule);
            refresh();
        } catch (InvalidExpressionException e) {
            Logger.getGlobal().log(Level.SEVERE, "Exception during enabling/disabling the tag rule.", e);
        }
    }

    /**
     * Opens edit dialog, when user doubleclicks on the table.
     */
    private void onMouseClick(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            onEditButtonClick(null);
        }
    }

    /**
     * Refreshes rules in the table.
     */
    private void refresh() {
        table.setItems(FXCollections.observableList(group.getRules()));
    }
}
