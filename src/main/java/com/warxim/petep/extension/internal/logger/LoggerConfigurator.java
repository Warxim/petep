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
package com.warxim.petep.extension.internal.logger;

import com.warxim.petep.gui.component.ConfigPane;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.util.FileUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Logger configurator.
 */
public class LoggerConfigurator extends ConfigPane<LoggerConfig> {
    @FXML
    private TextField pathInput;

    /**
     * Logger configurator constructor.
     * @throws IOException If the template could not be loaded
     */
    public LoggerConfigurator() throws IOException {
        super("/fxml/extension/internal/logger/LoggerConfigurator.fxml");
    }

    @Override
    public LoggerConfig getConfig() {
        return new LoggerConfig(pathInput.getText());
    }

    @Override
    public void setConfig(LoggerConfig config) {
        pathInput.setText(config.getPath());
    }

    @Override
    public boolean isValid() {
        if (pathInput.getText().length() == 0) {
            Dialogs.createErrorDialog("Path required", "You have to enter path.");
            return false;
        }

        return true;
    }

    /**
     * Let user choose output file for logging.
     */
    @FXML
    private void onFileOpenButtonClick(ActionEvent event) {
        // Choose log file
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Log file");

        if (pathInput.getText().isBlank()) {
            fileChooser.setInitialDirectory(new File(FileUtils.getProjectDirectory()));
        } else {
            var temp = new File(FileUtils.getProjectFileAbsolutePath(pathInput.getText()));
            fileChooser.setInitialDirectory(temp.getParentFile());
            fileChooser.setInitialFileName(temp.getName());
        }

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Log file (*.log)", "*.log"),
                new FileChooser.ExtensionFilter("Text file (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("All files (*.*)", "*.*")
        );

        var file = fileChooser.showSaveDialog(null);
        if (file == null) {
            return;
        }

        pathInput.setText(Paths.get(FileUtils.getProjectDirectory())
                .toAbsolutePath()
                .relativize(Paths.get(file.getAbsolutePath()))
                .toString()
                .replace('\\', '/'));
    }
}
