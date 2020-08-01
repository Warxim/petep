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

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.warxim.petep.exception.ConfigurationException;
import com.warxim.petep.wizard.project.WizardProjectExtension;

/** Static class for project extension list saving. */
public final class WizardExtensionsSaver {
  private WizardExtensionsSaver() {}

  /**
   * Save project extensions to specified configuration.
   *
   * @throws ConfigurationException
   */
  public static void save(String path, Collection<WizardProjectExtension> extensions)
      throws ConfigurationException {
    // Gson with pretty printing.
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    JsonArray list = new JsonArray(extensions.size());

    // Construct list of extensions for JSON.
    for (WizardProjectExtension extension : extensions) {
      JsonObject object = new JsonObject();

      object.addProperty("path", extension.getPath());
      object.add("store", extension.getStore());

      list.add(object);
    }

    // Write list of extensions to the configuration.
    try (JsonWriter writer = gson.newJsonWriter(new FileWriter(path))) {
      gson.toJson(list, writer);
    } catch (IOException e) {
      Logger.getGlobal()
          .log(Level.SEVERE, "PETEP wizard could not save extensions configuration.", e);
      throw new ConfigurationException("Project extensions could not be saved", e);
    }
  }
}
