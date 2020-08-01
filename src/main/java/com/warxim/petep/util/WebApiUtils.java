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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.warxim.petep.common.Constant;

public class WebApiUtils {
  private WebApiUtils() {}

  public static String getLatestVersion() {
    try {
      URL url = new URL(Constant.WEB + "api/version/");

      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
      connection.connect();

      String data = convertStreamToString(connection.getInputStream());
      JsonObject object = new Gson().fromJson(data, JsonObject.class);

      if (!object.has("version")) {
        logVersionFetchError();
        return Constant.VERSION;
      }

      return object.get("version").getAsString();
    } catch (Exception e) {
      logVersionFetchError();
    }

    return Constant.VERSION;
  }

  private static String convertStreamToString(InputStream stream) {
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

    StringBuilder builder = new StringBuilder();

    String line;
    try {
      while ((line = reader.readLine()) != null) {
        builder.append(line + "\n");
      }
    } catch (IOException e) {
      logVersionFetchError();
    } finally {
      try {
        stream.close();
      } catch (IOException e) {
        logVersionFetchError();
      }
    }

    return builder.toString();
  }

  private static void logVersionFetchError() {
    Logger.getGlobal().log(Level.SEVERE, "Could not fetch latest PETEP version info.");
  }
}
