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
package com.warxim.petep.extension.internal.history.gui.view.filter;

import com.warxim.petep.extension.internal.history.service.HistoryService;
import com.warxim.petep.gui.dialog.SimpleInputDialog;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Dialog for filtering historic tags
 */
public class HistoryFilterTagsDialog extends SimpleInputDialog<Set<String>> {
    @FXML
    private ListView<String> tagsInput;

    private Set<String> tags;

    /**
     * Constructs history filter tags dialog for choosing tags.
     * @param service Service for getting available tags
     * @param tags Set of tags to select by default
     * @throws IOException If the dialog template could not be loaded
     */
    public HistoryFilterTagsDialog(HistoryService service, Set<String> tags) throws IOException {
        super("/fxml/extension/internal/history/HistoryFilterTagsDialog.fxml", "History Filter Tags", "Set");
        this.tags = tags;
        service.getTags().thenAccept(this::initTags);

        var dialogPane = getDialogPane();
        var resetButtonType = new ButtonType("Reset", ButtonBar.ButtonData.LEFT);
        var clearButtonType = new ButtonType("Clear", ButtonBar.ButtonData.LEFT);
        dialogPane.getButtonTypes().add(0, resetButtonType);
        dialogPane.getButtonTypes().add(0, clearButtonType);
        dialogPane.lookupButton(clearButtonType).addEventFilter(ActionEvent.ACTION, this::clear);
        dialogPane.lookupButton(resetButtonType).addEventFilter(ActionEvent.ACTION, this::reset);
    }

    @Override
    protected Set<String> obtainResult() {
        return new HashSet<>(tagsInput.getSelectionModel().getSelectedItems());
    }

    @Override
    protected boolean isValid() {
        return true;
    }

    /**
     * Initializes tag input with specified tags.
     */
    private void initTags(Set<String> tags) {
        var tagList = new ArrayList<>(tags);
        tagList.sort(String::compareToIgnoreCase);
        tagsInput.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tagsInput.setItems(FXCollections.observableList(tagList));
        reset(null);
    }

    /**
     * Resets tag selection to original tags.
     */
    private void reset(ActionEvent event) {
        tags.forEach(tagsInput.getSelectionModel()::select);
        if (event != null) {
            event.consume();
        }
    }

    /**
     * Clears tag selection.
     */
    private void clear(ActionEvent event) {
        tagsInput.getSelectionModel().clearSelection();
        if (event != null) {
            event.consume();
        }
    }
}
