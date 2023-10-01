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

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.warxim.petep.common.Constant;
import com.warxim.petep.configuration.ProjectLoader;
import com.warxim.petep.exception.ConfigurationException;
import com.warxim.petep.util.FileUtils;
import com.warxim.petep.wizard.project.WizardProjectDecorator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Static class for wizard configuration loading.
 */
public final class WizardConfigurationLoader {
    private WizardConfigurationLoader() {
    }

    /**
     * Loads wizard configuration.
     * @param path Path to wizard configuration
     * @return List of project decorators or empty list (if wizard configuration cannot be loaded)
     */
    public static List<WizardProjectDecorator> load(String path) {
        // Create configuration if not exist.
        createIfNotExist(path);

        try (var reader = new JsonReader(new FileReader(path, Constant.FILE_CHARSET))) {
            // Parse list from configuration.

            var list = JsonParser.parseReader(reader).getAsJsonArray();
            var projects = new ArrayList<WizardProjectDecorator>(list.size());

            for (var i = 0; i < list.size(); ++i) {
                var maybeProjectDecrator = loadProjectDecorator(list.get(i));
                if (maybeProjectDecrator.isPresent()) {
                    projects.add(maybeProjectDecrator.get());
                }
            }

            return projects;
        } catch (JsonParseException e) {
            Logger.getGlobal().log(Level.SEVERE, "PETEP wizard configuration is invalid.", e);
        } catch (NoSuchFileException e) {
            Logger.getGlobal().info("PETEP wizard configuration doesn't exist.");
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "PETEP wizard could not read configuration.", e);
        }

        return new ArrayList<>();
    }

    /**
     * Loads project decorator from given JSON element.
     */
    private static Optional<WizardProjectDecorator> loadProjectDecorator(JsonElement element) {
        try {
            var configFile = FileUtils.getWorkingDirectoryFileAbsolutePath(
                    Path.of(element.getAsString())
                            .resolve(Constant.PROJECT_CONFIG_DIRECTORY)
                            .resolve(Constant.PROJECT_CONFIG_FILE)
                            .toString()
            );

            // Load project information.
            var project = ProjectLoader.load(configFile);

            var relativePath = element.getAsString();
            var lastModified = Instant.ofEpochMilli(new File(configFile).lastModified());

            // Create project decorator with path and date and add it to list of projects.
            return Optional.of(new WizardProjectDecorator(
                    project,
                    relativePath,
                    lastModified));
        } catch (ConfigurationException e) {
            Logger.getGlobal().log(Level.SEVERE, "PETEP wizard could not parse project configuration.", e);
        }
        return Optional.empty();
    }

    /**
     * Creates configuration with empty JSON array.
     */
    private static void createIfNotExist(String pathInput) {
        var path = Paths.get(pathInput);

        if (Files.exists(path)) {
            return;
        }

        try {
            Files.writeString(path, "[]", StandardOpenOption.CREATE);
        } catch (IOException e) {
            Logger.getGlobal()
                    .log(Level.SEVERE, "PETEP wizard could not create petep.json configuration.", e);
        }
    }
}
