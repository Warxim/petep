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

import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.gui.common.GuiConstant;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * Configuration pane for configuration of given generic type.
 * @param <C> Type of configuration
 */
@PetepAPI
public abstract class ConfigPane<C> extends AnchorPane {
    /**
     * Creates configuration pane from specified template and sets the object as controller.
     * @param template Path to FXML template
     * @throws IOException If the template could not be loaded
     */
    protected ConfigPane(String template) throws IOException {
        var loader = new FXMLLoader(getClass().getResource(template));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
        getStylesheets().add(GuiConstant.MAIN_CSS_PATH);
    }

    /**
     * Obtains configuration from pane.
     * @return Configuration created by the user in GUI
     */
    public abstract C getConfig();

    /**
     * Sets configuration to pane.
     * @param config Configuration to be set
     */
    public abstract void setConfig(C config);

    /**
     * Checks if configuration is valid.
     * @return {@code true} if the configuration in GUI is valid
     */
    public abstract boolean isValid();
}
