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
package com.warxim.petep.util;

import com.warxim.petep.extension.PetepAPI;

/** Web utils. */
@PetepAPI
public final class WebUtils {
  private WebUtils() {}

  /** Converts value to JavaScript parameter wrapped in double quotes. */
  public static String toJavaScriptParam(String value) {
    return "\"" + value.replace("\u0000", "\\0")
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t") + "\"";
  }

  /** Returns string with escaped HTML entities. */
  public static String escapeHtml(String value) {
    StringBuilder out = new StringBuilder(Math.max(16, value.length()));

    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);

      if (c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
        out.append("&#");
        out.append((int) c);
        out.append(';');
      } else {
        out.append(c);
      }
    }

    return out.toString();
  }
}
