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
package com.warxim.petep.wizard.dialog;

import java.io.File;
import java.io.IOException;
import java.util.List;
import com.warxim.petep.exception.ConfigurationException;
import com.warxim.petep.util.FileUtils;
import com.warxim.petep.wizard.configuration.WizardExtensionsLoader;
import com.warxim.petep.wizard.project.WizardProjectDecorator;
import com.warxim.petep.wizard.project.WizardProjectExtension;
import javafx.collections.FXCollections;

/** Edit project dialog. */
public final class EditProjectDialog extends ProjectDialog {
  public EditProjectDialog(WizardProjectDecorator project)
      throws IOException, ConfigurationException {
    super("Edit project", "Save");

    // Store project
    this.project = project;

    // Load extensions for the project
    List<WizardProjectExtension> list;
    list = WizardExtensionsLoader.load(FileUtils.getApplicationFileAbsolutePath(project.getPath())
        + File.separator + com.warxim.petep.common.Constant.PROJECT_CONFIG_DIRECTORY
        + File.separator + com.warxim.petep.common.Constant.EXTENSIONS_CONFIG_FILE);

    // Fill inputs
    nameInput.setText(project.getName());
    descriptionInput.setText(project.getDescription());

    extensions = FXCollections.observableArrayList(list);

    extensionsList.setItems(extensions);
  }
}
