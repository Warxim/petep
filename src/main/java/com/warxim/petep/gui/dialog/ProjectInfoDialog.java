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
package com.warxim.petep.gui.dialog;

import com.warxim.petep.extension.Extension;
import com.warxim.petep.project.Project;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;

/**
 * Project info dialog.
 */
public final class ProjectInfoDialog extends SimpleInfoDialog {
    // Labels.
    @FXML
    private Label nameLabel;
    @FXML
    private Label descriptionLabel;
    // Table.
    @FXML
    private TableView<Extension> extensionsTable;

    /**
     * Project info dialog constructor.
     * @param project Project to be shown
     * @param extensions List of extensions to be shown
     * @throws IOException If the dialog template could not be loaded
     */
    public ProjectInfoDialog(Project project, List<Extension> extensions) throws IOException {
        super("/fxml/dialog/ProjectInfo.fxml", "Project '" + project.getName() + "'");

        nameLabel.setText(project.getName());
        descriptionLabel.setText(project.getDescription());

        // Create cell value factories for extensions.
        for (var column : extensionsTable.getColumns()) {
            column.setCellValueFactory(new PropertyValueFactory<>(
                    column.getId().substring(0, column.getId().length() - "Column".length())));
        }

        // Add extensions to extensions table.
        extensionsTable.setItems(FXCollections.observableList(extensions));
    }
}
