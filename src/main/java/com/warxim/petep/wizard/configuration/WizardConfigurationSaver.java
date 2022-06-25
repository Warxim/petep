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
import com.warxim.petep.common.Constant;
import com.warxim.petep.util.GsonUtils;
import com.warxim.petep.wizard.project.WizardProjectDecorator;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Static class for wizard configuration saving.
 */
public final class WizardConfigurationSaver {
    private WizardConfigurationSaver() {
    }

    /**
     * Save wizard configuration.
     * @param path Path to wizard configuration
     * @param projects List or project decorators to save
     */
    public static void save(String path, Collection<WizardProjectDecorator> projects) {
        // Gson with pretty printing.
        var gson = GsonUtils.getGson();
        var list = new JsonArray(projects.size());

        // Create list of projects in json.
        for (var project : projects) {
            list.add(project.getPath());
        }

        // Write project list to configuration.
        try (var writer = gson.newJsonWriter(new FileWriter(path, Constant.FILE_CHARSET))) {
            gson.toJson(list, writer);
        } catch (NoSuchFileException e) {
            Logger.getGlobal().info("PETEP wizard configuration doesn't exist.");
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "PETEP wizard could not save configuration.", e);
        }
    }
}
