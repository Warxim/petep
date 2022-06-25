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
package com.warxim.petep.gui.guide;

import com.warxim.petep.extension.PetepAPI;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Guide base class.
 * (Guides are HTML pages that contain various tutorials, tips and tricks for extension modules and also PETEP core.)
 */
@PetepAPI
public abstract class Guide {
    protected Guide() {
    }

    /**
     * Gets title of the guide
     * @return Guide title
     */
    public abstract String getTitle();

    /**
     * Gets HTML of the guide
     * @return Guide content HTML
     */
    public abstract String getHtml();

    /**
     * Returns content of HTML file from resource path.
     * <p>If the resource does not exist, result will contain error message.</p>
     * @param path Path of HTML resource file to load into string
     */
    protected String loadHtmlResource(String path) {
        try (var in = getClass().getResourceAsStream(path)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "<p><b>Could not load " + getTitle() + " Guide:</b></p><p>" + e.getMessage() + "</p>";
        }
    }

    /**
     * Returns text displayed in list view in guide dialog.
     */
    public String toString() {
        return getTitle();
    }
}
