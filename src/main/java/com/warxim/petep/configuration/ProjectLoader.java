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
package com.warxim.petep.configuration;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.warxim.petep.common.Constant;
import com.warxim.petep.exception.ConfigurationException;
import com.warxim.petep.project.Project;
import com.warxim.petep.util.GsonUtils;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

/**
 * Static class for project loading.
 */
public final class ProjectLoader {
    private ProjectLoader() {
    }

    /**
     * Loads project from specified path.
     * @param path Path to project configuration file
     * @return Loaded project
     * @throws ConfigurationException If the project could not be loaded
     */
    public static Project load(String path) throws ConfigurationException {
        try (var reader = new JsonReader(new FileReader(path, Constant.FILE_CHARSET))) {
            return GsonUtils.getGson().fromJson(JsonParser.parseReader(reader), Project.class);
        } catch (JsonParseException e) {
            throw new ConfigurationException("Could not parse project configuration!", e);
        } catch (NoSuchFileException e) {
            throw new ConfigurationException("Could not find project configuration!", e);
        } catch (IOException e) {
            throw new ConfigurationException("Could not load project configuration!", e);
        }
    }
}
