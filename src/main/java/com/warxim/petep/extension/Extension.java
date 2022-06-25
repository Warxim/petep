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
package com.warxim.petep.extension;

import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.GuiHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Superclass for extensions.
 * <p>Each extension is identified by its code, so it has to be unique across the whole application.</p>
 */
@Getter
@PetepAPI
@AllArgsConstructor
public abstract class Extension {
    /**
     * Path where the extension .jar file is located.
     */
    private final String path;

    /**
     * Initializes the extension.
     * @param helper Helper for working with PETEP
     */
    public void init(ExtensionHelper helper) {}

    /**
     * Runs before extensions are initialized.
     * @param helper Helper for working with PETEP
     */
    public void beforeInit(ExtensionHelper helper) {}

    /**
     * Runs after extensions are initialized.
     * @param helper Helper for working with PETEP
     */
    public void afterInit(ExtensionHelper helper) {}

    /**
     * Initializes the extension GUI.
     * @param helper Helper for working with PETEP GUI
     */
    public void initGui(GuiHelper helper) {}

    /**
     * Runs before extension GUIs are initialized.
     * @param helper Helper for working with PETEP GUI
     */
    public void beforeInitGui(GuiHelper helper) {}

    /**
     * Runs after extension GUIs are initialized.
     * @param helper Helper for working with PETEP GUI
     */
    public void afterInitGui(GuiHelper helper) {}

    /**
     * Destroys the extension.
     */
    public void destroy() {}

    /**
     * Obtains extension code.
     * @return Code of the extension
     */
    public abstract String getCode();

    /**
     * Obtains extension name.
     * @return Name of the extension
     */
    public abstract String getName();

    /**
     * Obtains extension description.
     * @return Description of the extension
     */
    public abstract String getDescription();

    /**
     * Obtains extension version.
     * @return Version of the extension
     */
    public abstract String getVersion();
}
