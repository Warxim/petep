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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.warxim.booleanexpressioninterpreter.ExpressionParser;
import com.warxim.booleanexpressioninterpreter.InvalidExpressionException;
import com.warxim.petep.extension.internal.tagger.factory.TagSubrule;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleFactoryManager;
import com.warxim.petep.extension.internal.tagger.gui.subrule.EditTagSubruleDialog;
import com.warxim.petep.extension.internal.tagger.gui.subrule.NewTagSubruleDialog;
import com.warxim.petep.extension.internal.tagger.rule.TagRule;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.gui.dialog.SimpleInputDialog;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/** Tag rule dialog. */
public abstract class TagRuleDialog extends SimpleInputDialog<TagRule> {
  private static final String INDEX_REGEX =
      "[^0-9()!]{0}[^0-9()!]|[^0-9()!]{0}$|^{0}$|^{0}[^0-9()!]";

  private TagSubruleFactoryManager moduleManager;

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
  private TableView<TagSubrule> subrulesTable;

  @FXML
  private TableColumn<TagSubrule, Integer> indexColumn;

  @FXML
  private TableColumn<TagSubrule, String> typeColumn;

  protected ObservableList<TagSubrule> subrules;

  private TagRule rule;

  /** Tag rule constructor. */
  public TagRuleDialog(String title, String okText, TagSubruleFactoryManager moduleManager)
      throws IOException {
    super("/fxml/extension/internal/tagger/TagRuleDialog.fxml", title, okText);

    this.moduleManager = moduleManager;

    enabledInput.setSelected(true);

    // Event when on custom expression checkbox is selected / deselected.
    customExpressionInput.selectedProperty().addListener(this::onCustomExpressionSelectionChange);

    // Run prepare on expression input change.
    expressionInput.focusedProperty().addListener(this::onExpressionInputFocusChange);

    // Allow only specific characters for expression input.
    expressionInput.textProperty().addListener(this::onExpressionInputTextChange);

    // Index number calculation.
    indexColumn.setCellFactory(this::createIndexCell);

    typeColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().toString()));

    subrules = FXCollections.observableArrayList();
    subrulesTable.setItems(subrules);
  }

  /** Generates expression using logical ANDs. */
  protected final String generateExpression() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < subrules.size(); ++i) {
      builder.append(i);
      builder.append(" & ");
    }

    if (builder.length() > 3) {
      builder.delete(builder.length() - 3, builder.length());
    }

    return builder.toString();
  }

  private final TableCell<TagSubrule, Integer> createIndexCell(
      TableColumn<TagSubrule, Integer> column) {
    TableCell<TagSubrule, Integer> cell = new TableCell<>();
    cell.textProperty()
        .bind(Bindings.createStringBinding(
            () -> cell.isEmpty() ? null : Integer.toString(cell.getIndex()), cell.emptyProperty(),
            cell.indexProperty()));
    return cell;
  }

  private final void onCustomExpressionSelectionChange(
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

  private final void onExpressionInputTextChange(
      ObservableValue<? extends String> observable,
      String oldValue,
      String newValue) {
    if (!newValue.matches("\\s0-9!&\\(\\)\\{\\}\\[\\]\\|\\^")) {
      expressionInput.setText(newValue.replaceAll("[^\\s0-9!&\\(\\)\\{\\}\\[\\]\\^\\|]", ""));
    }
  }

  private final void onExpressionInputFocusChange(
      ObservableValue<? extends Boolean> observable,
      boolean oldValue,
      boolean newValue) {
    if (oldValue) {
      try {
        expressionInput
            .setText(ExpressionParser.prepare(expressionInput.getText()).replace("! ", "!"));
      } catch (InvalidExpressionException e) {
        Dialogs.createErrorDialog("Expression is invalid", "Entered expression is invalid!");
      }
    }
  }

  /** Refreshes expression if custom expression is disabled. */
  private final void refreshExpression() {
    if (!customExpressionInput.isSelected()) {
      expressionInput.setText(generateExpression());
    }
  }

  @FXML
  private final void onNewButtonClick(ActionEvent event) {
    try {
      NewTagSubruleDialog dialog = new NewTagSubruleDialog(moduleManager);

      Optional<TagSubrule> data = dialog.showAndWait();

      if (!data.isPresent() || data.get() == null) {
        return;
      }

      subrules.add(data.get());

      refreshExpression();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Exception during openning of tag rule dialog", e);
    }
  }

  @FXML
  private final void onEditButtonClick(ActionEvent event) {
    TagSubrule subrule = subrulesTable.getSelectionModel().getSelectedItem();

    if (subrule == null) {
      return;
    }

    try {
      EditTagSubruleDialog dialog = new EditTagSubruleDialog(moduleManager, subrule);

      Optional<TagSubrule> data = dialog.showAndWait();

      if (!data.isPresent() || data.get() == null) {
        return;
      }

      subrules.set(subrules.indexOf(subrule), data.get());
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Exception during openning of tag rule dialog", e);
    }
  }

  @FXML
  private final void onRemoveButtonClick(ActionEvent event) {
    int index = subrulesTable.getSelectionModel().getSelectedIndex();

    if (index == -1) {
      return;
    }

    if (!Dialogs.createYesOrNoDialog("Are you sure?", "Do you really want to remove subrule?")) {
      return;
    }

    subrules.remove(index);

    if (customExpressionInput.isSelected()) {
      try {
        expressionInput.setText(ExpressionParser.prepare(
            expressionInput.getText().replaceAll(MessageFormat.format(INDEX_REGEX, index), "")));
      } catch (InvalidExpressionException e) {
        Dialogs.createErrorDialog("Expression is invalid", "Entered expression is invalid!");
      }
    } else {
      refreshExpression();
    }
  }

  private final void swap(int what, int with) {
    Collections.swap(subrules, what, with);

    if (customExpressionInput.isSelected()) {
      String newExpression = expressionInput.getText()
          .replace("!", "! ")
          .replaceAll(MessageFormat.format(INDEX_REGEX, what), "_TMP_");

      newExpression =
          newExpression.replaceAll(MessageFormat.format(INDEX_REGEX, with), String.valueOf(what));

      newExpression = newExpression.replace("_TMP_", String.valueOf(with));

      try {
        expressionInput.setText(ExpressionParser.prepare(newExpression).replace("! ", "!"));
      } catch (InvalidExpressionException e) {
        Dialogs.createErrorDialog("Expression is invalid", "Entered expression is invalid!");
      }
    }
  }

  /** Move instance up */
  @FXML
  private final void onMoveUpButtonClick(ActionEvent event) {
    int index = subrulesTable.getSelectionModel().getSelectedIndex();

    if (index <= 0) {
      return;
    }

    swap(index, index - 1);
  }

  /** Move instance down */
  @FXML
  private final void onMoveDownButtonClick(ActionEvent event) {
    int index = subrulesTable.getSelectionModel().getSelectedIndex();

    if (index == -1 || index == subrules.size() - 1) {
      return;
    }

    swap(index, index + 1);
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
      Dialogs.createErrorDialog("Tag required",
          "You have to enter tag (allowed characters are A-Za-z0-9-_.).");
      return false;
    }

    try {
      rule = new TagRule(nameInput.getText(), descriptionInput.getText(), enabledInput.isSelected(),
          tagInput.getText(), new ArrayList<>(subrules), expressionInput.getText());
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
}
