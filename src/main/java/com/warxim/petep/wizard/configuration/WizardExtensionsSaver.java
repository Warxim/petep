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
package com.warxim.petep.wizard.configuration;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.warxim.petep.common.Constant;
import com.warxim.petep.exception.ConfigurationException;
import com.warxim.petep.util.GsonUtils;
import com.warxim.petep.wizard.project.WizardProjectExtension;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

/**
 * Static class for project extension list saving.
 */
public final class WizardExtensionsSaver {
    private WizardExtensionsSaver() {
    }

    /**
     * Save project extensions to specified configuration.
     * @param path Path to project extensions configuration
     * @param extensions Extensions to be saved to the project
     * @throws ConfigurationException If it was not possible to save the configuration
     */
    public static void save(String path, Collection<WizardProjectExtension> extensions)
            throws ConfigurationException {
        // Gson with pretty printing.
        var gson = GsonUtils.getGson();
        var list = new JsonArray(extensions.size());

        // Construct list of extensions for JSON.
        for (var extension : extensions) {
            var object = new JsonObject();

            object.addProperty("path", extension.getPath());
            object.add("store", extension.getStore());
            object.add("config", extension.getConfig());

            list.add(object);
        }

        // Write list of extensions to the configuration.
        try (var writer = gson.newJsonWriter(new FileWriter(path, Constant.FILE_CHARSET))) {
            gson.toJson(list, writer);
        } catch (IOException e) {
            throw new ConfigurationException("Project extensions could not be saved", e);
        }
    }
}
