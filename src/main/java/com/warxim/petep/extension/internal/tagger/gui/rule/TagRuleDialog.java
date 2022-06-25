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

import com.warxim.booleanexpressioninterpreter.InvalidExpressionException;
import com.warxim.petep.extension.internal.tagger.factory.TagSubrule;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleFactoryManager;
import com.warxim.petep.extension.internal.tagger.gui.subrule.EditTagSubruleDialog;
import com.warxim.petep.extension.internal.tagger.gui.subrule.NewTagSubruleDialog;
import com.warxim.petep.extension.internal.tagger.rule.TagRule;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.gui.dialog.SimpleInputDialog;
import com.warxim.petep.util.GuiUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tag rule dialog.
 */
public abstract class TagRuleDialog extends SimpleInputDialog<TagRule> {
    private static final String INDEX_REGEX = "([^0-9]*){0}([^0-9]*)";
    @FXML
    protected TextField nameInput;
    @FXML
    protected TextArea descriptionInput;
    @FXML
    protected TextField tagInput;
    @FXML
    protected CheckBox enabledInput;
    @FXML
    protected TextField expressionInput;
    @FXML
    protected CheckBox customExpressionInput;
    @FXML
    protected TableView<TagSubrule> subrulesTable;
    @FXML
    protected TableColumn<TagSubrule, Integer> indexColumn;
    @FXML
    protected TableColumn<TagSubrule, String> typeColumn;

    protected ObservableList<TagSubrule> subrules;
    protected TagSubruleFactoryManager factoryManager;
    protected TagRule rule;

    /**
     * Constructs tag rule dialog.
     * @param title Title of the dialog
     * @param okText Text of the OK button
     * @param factoryManager Manager of tag subrule factories
     * @throws IOException If the dialog template could not be loaded
     */
    protected TagRuleDialog(String title, String okText, TagSubruleFactoryManager factoryManager) throws IOException {
        super("/fxml/extension/internal/tagger/TagRuleDialog.fxml", title, okText);

        this.factoryManager = factoryManager;

        enabledInput.setSelected(true);

        // Event when on custom expression checkbox is selected / deselected.
        customExpressionInput.selectedProperty().addListener(this::onCustomExpressionSelectionChange);

        // Allow only specific characters for expression input.
        expressionInput.textProperty().addListener(this::onExpressionInputTextChange);

        // Index number calculation.
        indexColumn.setCellFactory(this::createIndexCell);

        typeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().toString()));

        subrules = FXCollections.observableArrayList();
        subrulesTable.setItems(subrules);

        subrulesTable.setOnMousePressed(this::onMouseClick);

        expressionInput.setTooltip(GuiUtils.createTooltip(
                "Use subrule indexes (zero-based), parenthesis (){}[] and logical operators !&|^"
        ));
    }

    @Override
    protected final boolean isValid() {
        // Validate name.
        if (nameInput.getText().isBlank()) {
            Dialogs.createErrorDialog("Name required", "You have to enter name.");
            return false;
        }

        // Validate tag.
        if (tagInput.getText().isEmpty() || !tagInput.getText().matches("^[a-zA-Z0-9-_.]+$")) {
            Dialogs.createErrorDialog("Tag required", "You have to enter tag (allowed characters are A-Za-z0-9-_.).");
            return false;
        }

        try {
            rule = new TagRule(
                    nameInput.getText(),
                    descriptionInput.getText(),
                    enabledInput.isSelected(),
                    tagInput.getText(),
                    new ArrayList<>(subrules),
                    expressionInput.getText());
        } catch (InvalidExpressionException e) {
            Dialogs.createErrorDialog("Invalid expression", "Entered expression is not valid!");
            return false;
        }

        return true;
    }

    @Override
    protected final TagRule obtainResult() {
        return rule;
    }

    /**
     * Generates expression using logical ANDs.
     */
    protected final String generateExpression() {
        var builder = new StringBuilder();
        for (int i = 0; i < subrules.size(); ++i) {
            builder.append(i);
            builder.append(" & ");
        }

        if (builder.length() > 3) {
            builder.delete(builder.length() - 3, builder.length());
        }

        return builder.toString();
    }

    /**
     * Shows dialog for creating new tag subrule and adds the created subrule to list.
     */
    @FXML
    private void onNewButtonClick(ActionEvent event) {
        try {
            var dialog = new NewTagSubruleDialog(factoryManager);
            var data = dialog.showAndWait();

            if (data.isEmpty()) {
                return;
            }

            subrules.add(data.get());

            refreshExpression();
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Exception during openning of tag rule dialog", e);
        }
    }

    /**
     * Shows dialog for editing existing tag subrule.
     */
    @FXML
    private void onEditButtonClick(ActionEvent event) {
        var subrule = subrulesTable.getSelectionModel().getSelectedItem();
        if (subrule == null) {
            return;
        }

        try {
            var dialog = new EditTagSubruleDialog(factoryManager, subrule);
            var data = dialog.showAndWait();

            if (data.isEmpty()) {
                return;
            }

            subrules.set(subrules.indexOf(subrule), data.get());
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Exception during openning of tag rule dialog", e);
        }
    }

    /**
     * Removes selected tag subrule.
     */
    @FXML
    private void onRemoveButtonClick(ActionEvent event) {
        int index = subrulesTable.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            return;
        }

        if (!Dialogs.createYesOrNoDialog("Are you sure?", "Do you really want to remove subrule?")) {
            return;
        }

        subrules.remove(index);

        if (customExpressionInput.isSelected()) {
            var newExpression = expressionInput.getText().replaceAll(
                    MessageFormat.format(INDEX_REGEX, index),
                    "$1$2");
            expressionInput.setText(newExpression);
        } else {
            refreshExpression();
        }
    }

    /**
     * Move instance up in the list of tag subrules (to the right).
     */
    @FXML
    private void onMoveUpButtonClick(ActionEvent event) {
        int index = subrulesTable.getSelectionModel().getSelectedIndex();
        if (index <= 0) {
            return;
        }

        swapSubrules(index, index - 1);
    }

    /**
     * Move instance down in the list of tag subrules (to the left).
     */
    @FXML
    private void onMoveDownButtonClick(ActionEvent event) {
        int index = subrulesTable.getSelectionModel().getSelectedIndex();
        if (index == -1 || index == subrules.size() - 1) {
            return;
        }

        swapSubrules(index, index + 1);
    }

    /**
     * Opens edit dialog when doubleclicking on table item.
     */
    private void onMouseClick(MouseEvent event) {
        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
            onEditButtonClick(null);
        }
    }

    /**
     * Creates cell with index number as a value.
     */
    private TableCell<TagSubrule, Integer> createIndexCell(TableColumn<TagSubrule, Integer> column) {
        var cell = new TableCell<TagSubrule, Integer>();
        cell.textProperty().bind(Bindings.createStringBinding(
                () -> cell.isEmpty()
                        ? null
                        : Integer.toString(cell.getIndex()),
                cell.emptyProperty(),
                cell.indexProperty()
        ));
        return cell;
    }

    /**
     * Enables/disables expression input, when custom expression checkbox is checked/unchecked.
     */
    private void onCustomExpressionSelectionChange(
            ObservableValue<? extends Boolean> observable,
            boolean oldValue,
            boolean newValue) {
        if (newValue) {
            expressionInput.setDisable(false);
        } else {
            expressionInput.setDisable(true);
            refreshExpression();
        }
    }

    /**
     * Removes invalid characters from expression input on change.
     */
    private void onExpressionInputTextChange(ObservableValue<? extends String> observable,
                                                   String oldValue,
                                                   String newValue) {
        if (!newValue.matches("\\s0-9!&\\(\\)\\{\\}\\[\\]\\|\\^")) {
            expressionInput.setText(newValue.replaceAll("[^\\s0-9!&\\(\\)\\{\\}\\[\\]\\^\\|]", ""));
        }
    }

    /**
     * Refreshes expression if custom expression is disabled.
     */
    private void refreshExpression() {
        if (!customExpressionInput.isSelected()) {
            expressionInput.setText(generateExpression());
        }
    }

    /**
     * Swaps sb rules at given indexes (and fixes regex).
     */
    private void swapSubrules(int what, int with) {
        Collections.swap(subrules, what, with);

        if (customExpressionInput.isSelected()) {
            var newExpression = expressionInput.getText()
                    .replace("!", "! ")
                    .replaceAll(
                            MessageFormat.format(INDEX_REGEX, what),
                            "$1_TMP_$2")
                    .replaceAll(
                            MessageFormat.format(INDEX_REGEX, with),
                            "$1" + what + "$2")
                    .replace("_TMP_", String.valueOf(with));
            expressionInput.setText(newExpression);
        }
    }
}
