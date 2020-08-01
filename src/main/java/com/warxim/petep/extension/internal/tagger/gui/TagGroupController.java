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

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.warxim.petep.extension.internal.common.rule_group.RuleGroup;
import com.warxim.petep.extension.internal.common.rule_group.gui.RuleGroupController;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleFactoryManager;
import com.warxim.petep.extension.internal.tagger.gui.rule.EditTagRuleDialog;
import com.warxim.petep.extension.internal.tagger.gui.rule.NewTagRuleDialog;
import com.warxim.petep.extension.internal.tagger.rule.TagRule;
import com.warxim.petep.gui.dialog.Dialogs;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/** Tag group controller. */
public final class TagGroupController extends RuleGroupController<TagRule>
    implements Initializable {
  private final TagSubruleFactoryManager moduleManager;

  @FXML
  private TableView<TagRule> table;

  @FXML
  private TableColumn<TagRule, String> nameColumn;

  @FXML
  private TableColumn<TagRule, String> tagColumn;

  @FXML
  private TableColumn<TagRule, Boolean> enabledColumn;

  /** Tag group controller constructor. */
  public TagGroupController(RuleGroup<TagRule> group, TagSubruleFactoryManager moduleManager) {
    super(group);
    this.moduleManager = moduleManager;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    tagColumn.setCellValueFactory(new PropertyValueFactory<>("tag"));
    enabledColumn.setCellValueFactory(new PropertyValueFactory<>("enabled"));

    table.setItems(FXCollections.observableList(group.getRules()));
  }

  @FXML
  private void onNewButtonClick(ActionEvent event) {
    try {
      NewTagRuleDialog dialog = new NewTagRuleDialog(moduleManager);

      Optional<TagRule> data = dialog.showAndWait();

      if (!data.isPresent() || data.get() == null) {
        return;
      }

      group.addRule(data.get());

      refresh();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Exception during openning of tag rule dialog", e);
    }
  }

  @FXML
  private void onEditButtonClick(ActionEvent event) {
    TagRule rule = table.getSelectionModel().getSelectedItem();

    if (rule == null) {
      return;
    }

    try {
      EditTagRuleDialog dialog = new EditTagRuleDialog(moduleManager, rule);

      Optional<TagRule> data = dialog.showAndWait();

      if (!data.isPresent() || data.get() == null) {
        return;
      }

      group.replace(rule, data.get());

      refresh();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Exception during openning of tag rule dialog", e);
    }
  }

  @FXML
  private void onRemoveButtonClick(ActionEvent event) {
    TagRule rule = table.getSelectionModel().getSelectedItem();

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

  @FXML
  private void onMoveUpButtonClick(ActionEvent event) {
    int index = table.getSelectionModel().getSelectedIndex();

    if (index <= 0) {
      return;
    }

    group.swapRules(index, index - 1);

    refresh();
  }

  @FXML
  private void onMoveDownButtonClick(ActionEvent event) {
    int index = table.getSelectionModel().getSelectedIndex();

    if (index == -1 || index == group.ruleCount() - 1) {
      return;
    }

    group.swapRules(index, index + 1);

    refresh();
  }

  private void refresh() {
    table.setItems(FXCollections.observableList(group.getRules()));
  }
}
