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
package com.warxim.petep.extension.internal.scripter.gui;

import com.warxim.petep.extension.internal.common.rulegroup.RuleGroup;
import com.warxim.petep.extension.internal.common.rulegroup.gui.RuleGroupController;
import com.warxim.petep.extension.internal.scripter.gui.rule.EditScriptDialog;
import com.warxim.petep.extension.internal.scripter.gui.rule.NewScriptDialog;
import com.warxim.petep.extension.internal.scripter.helper.ScriptHelperFactory;
import com.warxim.petep.extension.internal.scripter.rule.FileScript;
import com.warxim.petep.extension.internal.scripter.rule.Script;
import com.warxim.petep.extension.internal.scripter.rule.ScriptType;
import com.warxim.petep.extension.internal.scripter.rule.StringScript;
import com.warxim.petep.gui.dialog.Dialogs;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
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
 * Controller for single script group.
 */
public class ScriptGroupController extends RuleGroupController<Script> implements Initializable  {
    @FXML
    private TableView<Script> table;
    @FXML
    private TableColumn<Script, String> nameColumn;
    @FXML
    private TableColumn<Script, String> languageColumn;
    @FXML
    private TableColumn<Script, ScriptType> typeColumn;
    @FXML
    private TableColumn<Script, Boolean> enabledColumn;
    @FXML
    private TableColumn<Script, String> dataColumn;

    private ScriptHelperFactory scriptHelperFactory;

    /**
     * Script group controller constructor.
     * @param group Group of rules to show in the controller
     * @param scriptHelperFactory Manager of factories for creation of script helpers
     */
    public ScriptGroupController(RuleGroup<Script> group, ScriptHelperFactory scriptHelperFactory) {
        super(group);
        this.scriptHelperFactory = scriptHelperFactory;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        languageColumn.setCellValueFactory(new PropertyValueFactory<>("language"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        enabledColumn.setCellValueFactory(new PropertyValueFactory<>("enabled"));
        dataColumn.setCellValueFactory(this::dataCellFactory);

        table.setItems(FXCollections.observableList(group.getRules()));

        table.setOnMousePressed(this::onMouseClick);

        initContextMenu();
    }

    /**
     * Shows dialog for creating new script and adds it to the group.
     */
    @FXML
    private void onNewButtonClick(ActionEvent event) {
        try {
            var dialog = new NewScriptDialog(scriptHelperFactory);
            var maybeScript = dialog.showAndWait();

            if (maybeScript.isEmpty()) {
                return;
            }

            group.addRule(maybeScript.get());

            refresh();
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Exception during openning of modify rule dialog", e);
        }
    }

    /**
     * Shows dialog for Editing of existing script.
     */
    @FXML
    private void onEditButtonClick(ActionEvent event) {
        var script = table.getSelectionModel().getSelectedItem();
        if (script == null) {
            return;
        }

        try {
            var dialog = new EditScriptDialog(scriptHelperFactory, script);
            var maybeScript = dialog.showAndWait();
            if (maybeScript.isEmpty()) {
                return;
            }

            group.replace(script, maybeScript.get());

            refresh();
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Exception during openning of modify rule dialog", e);
        }
    }

    /**
     * Removes selected script from group.
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
     * Moves selected script up in the group.
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
     * Moves selected script down in the group.
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
     * Initializes context menu on table for performing actions with scripts.
     */
    private void initContextMenu() {
        var contextMenu = new ContextMenu();

        var editItem = new MenuItem("Edit");
        editItem.setOnAction(this::onEditButtonClick);

        var reloadItem = new MenuItem("Reload");
        reloadItem.setOnAction(this::onReloadClick);

        var enableDisableItem = new MenuItem("Enable/Disable");
        enableDisableItem.setOnAction(this::onEnableDisableClick);

        var duplicateItem = new MenuItem("Duplicate");
        duplicateItem.setOnAction(this::onDuplicateButtonClick);

        var removeItem = new MenuItem("Remove");
        removeItem.setOnAction(this::onRemoveButtonClick);

        contextMenu.getItems().addAll(editItem, reloadItem, enableDisableItem, duplicateItem, removeItem);

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

        var newRule = rule.copy(scriptHelperFactory);
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

        Script newRule;
        if (rule instanceof FileScript) {
            newRule = new FileScript(
                    rule.getName(),
                    rule.getDescription(),
                    !rule.isEnabled(),
                    rule.getLanguage(),
                    scriptHelperFactory,
                    ((FileScript) rule).getPath());
        } else {
            newRule = new StringScript(
                    rule.getName(),
                    rule.getDescription(),
                    !rule.isEnabled(),
                    rule.getLanguage(),
                    scriptHelperFactory,
                    ((StringScript) rule).getScript());
        }
        group.replace(rule, newRule);
        refresh();
    }

    /**
     * Reloads selected script. (This can be used for reloading edited file scripts.)
     */
    private void onReloadClick(ActionEvent event) {
        var rule = table.getSelectionModel().getSelectedItem();
        if (rule == null) {
            return;
        }

        var newRule = rule.copy(scriptHelperFactory);
        group.replace(rule, newRule);
        refresh();
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
     * Creates cell factory for script cell.
     */
    private ObservableValue<String> dataCellFactory(TableColumn.CellDataFeatures<Script, String> cell) {
        var script = cell.getValue();
        if (script.getType() == ScriptType.STRING) {
            return new SimpleStringProperty("Script (" + countLines(((StringScript) script).getScript()) + " LoC)");
        }
        return new SimpleStringProperty(((FileScript) script).getPath());
    }

    /**
     * Refreshes the scripts in the table.
     */
    private void refresh() {
        table.setItems(FXCollections.observableList(group.getRules()));
    }

    /**
     * Returns number of lines in string.
     */
    private static int countLines(String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        var lines = 1;
        var position = 0;
        while ((position = str.indexOf('\n', position) + 1) != 0) {
            ++lines;
        }
        return lines;
    }
}
