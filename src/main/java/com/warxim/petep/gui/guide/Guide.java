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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import com.warxim.petep.extension.PetepAPI;

/**
 * Guide base class. (Guides are HTML pages that contain various tutorials, tips and tricks for
 * extension modules and also PETEP core.)
 */
@PetepAPI
public abstract class Guide {
  public Guide() {}

  public abstract String getTitle();

  public abstract String getHtml();

  /** Returns content of HTML file from resource path. */
  protected String loadHtmlResource(String path) {
    try (InputStream in = getClass().getResourceAsStream(path)) {
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      return "<p><b>Could not load " + getTitle() + " Guide:</b></p><p>" + e.getMessage() + "</p>";
    }
  }

  /** Returns text displayed in list view in guide dialog. */
  public String toString() {
    return getTitle();
  }
}
