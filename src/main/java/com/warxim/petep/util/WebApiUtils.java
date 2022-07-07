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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.warxim.petep.common.Constant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilities for accessing PETEP web API.
 */
public final class WebApiUtils {
    private WebApiUtils() {
    }

    /**
     * Obtains latest available version of PETEP.
     * @return Version string of latest PETEP
     */
    public static String getLatestVersion() {
        try {
            var url = new URL(Constant.WEB + "api/version/");

            var connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            connection.connect();

            var data = readStreamToString(connection.getInputStream());
            var object = new Gson().fromJson(data, JsonObject.class);

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

    /**
     * Reads input stream into string.
     */
    private static String readStreamToString(InputStream stream) {
        var reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        var builder = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
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

    /**
     * Logs version fetch error.
     */
    private static void logVersionFetchError() {
        Logger.getGlobal().log(Level.SEVERE, "Could not fetch latest PETEP version info.");
    }
}
