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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.warxim.petep.common.Constant;
import com.warxim.petep.wizard.project.WizardProjectDecorator;
import com.warxim.petep.wizard.project.WizardProjectExtension;
import javafx.collections.FXCollections;

/** New project dialog. */
public final class NewProjectDialog extends ProjectDialog {
  public NewProjectDialog(String path) throws IOException {
    super("New project", "Create");

    // Create new project decorator for nonexisting (null) project.
    this.project = new WizardProjectDecorator(null, path, null);

    // Initialize list of extensions.
    List<WizardProjectExtension> list = new ArrayList<>(Constant.INTERNAL_EXTENSIONS.size());

    for (String extension : Constant.INTERNAL_EXTENSIONS) {
      list.add(new WizardProjectExtension(extension, null));
    }

    extensions = FXCollections.observableArrayList(list);

    extensionsList.setItems(extensions);
  }
}
