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

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.warxim.petep.exception.ConfigurationException;
import com.warxim.petep.wizard.project.WizardProjectExtension;

/** Static class for project extension list loading. */
public final class WizardExtensionsLoader {
  private WizardExtensionsLoader() {}

  /**
   * Loads project extensions from specified configuration.
   *
   * @throws ConfigurationException
   */
  public static List<WizardProjectExtension> load(String path) throws ConfigurationException {
    try (JsonReader reader = new JsonReader(new FileReader(path))) {
      // Parse array from configuration.
      JsonArray list = JsonParser.parseReader(reader).getAsJsonArray();

      List<WizardProjectExtension> extensions = new ArrayList<>(list.size());

      // Parse extensions.
      for (int i = 0; i < list.size(); ++i) {
        JsonObject object = list.get(i).getAsJsonObject();

        extensions
            .add(new WizardProjectExtension(object.get("path").getAsString(), object.get("store")));
      }

      return extensions;
    } catch (JsonParseException e) {
      throw new ConfigurationException("PETEP wizard configuration is invalid.", e);
    } catch (NoSuchFileException e) {
      throw new ConfigurationException("PETEP wizard configuration doesn't exist.", e);
    } catch (IOException e) {
      throw new ConfigurationException("PETEP wizard could not read configuration.", e);
    }
  }
}
