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
package com.warxim.petep.extension.internal.external_http_proxy.lighthttp;

import java.io.IOException;
import java.io.InputStream;

public final class LightHttpUtils {
  private LightHttpUtils() {}

  public static int readByte(InputStream in) throws IOException {
    int c = in.read();

    if (c == -1) {
      throw new IOException("End of stream reached!");
    }

    return c;
  }

  public static void skipNBytes(int n, InputStream in) throws IOException {
    if (in.skip(n) != n) {
      throw new IOException("Could not skip n bytes!");
    }
  }
}
