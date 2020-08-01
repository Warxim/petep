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
package com.warxim.petep.gui.component;

import java.io.IOException;
import com.warxim.petep.extension.PetepAPI;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

/** Configuration pane for configuration of given generic type. */
@PetepAPI
public abstract class ConfigPane<C> extends AnchorPane {
  /** Creates configuration pane from specified template and sets the object as controller. */
  public ConfigPane(String template) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource(template));
    loader.setRoot(this);
    loader.setController(this);
    loader.load();
    getStylesheets().add("/css/Main.css");
  }

  /** Returns configuration from pane. */
  public abstract C getConfig();

  /** Sets configuration to pane. */
  public abstract void setConfig(C config);

  /** Checks if configuration is valid. */
  public abstract boolean isValid();
}
