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

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.NoSuchFileException;
import java.util.Collection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.warxim.petep.common.Constant;
import com.warxim.petep.exception.ConfigurationException;
import com.warxim.petep.extension.Extension;
import com.warxim.petep.persistence.Configurable;
import com.warxim.petep.persistence.Storable;
import com.warxim.petep.util.ExtensionUtils;

/** Static class for saving extensions. */
public final class ExtensionsSaver {

  private ExtensionsSaver() {}

  /** Saves extensions to specified path. */
  public static void save(String path, Collection<Extension> extensions)
      throws ConfigurationException {
    // Enable pretty printing.
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    JsonArray list = new JsonArray(extensions.size());

    for (Extension extension : extensions) {
      JsonObject object = new JsonObject();

      // Save extension path.
      object.addProperty(Constant.CONFIG_ITEM_PATH, extension.getPath());

      // Save extension store.
      Type storeType = ExtensionUtils.getStoreType(extension);
      if (storeType != null) {
        object.add(Constant.CONFIG_ITEM_STORE,
            gson.toJsonTree(((Storable<?>) extension).saveStore(), storeType));
      }

      // Save extension config.
      Type configType = ExtensionUtils.getConfigType(extension);
      if (configType != null) {
        object.add(Constant.CONFIG_ITEM_CONFIG,
            gson.toJsonTree(((Configurable<?>) extension).saveConfig(), configType));
      }

      list.add(object);
    }

    // Write json to specified path.
    try (JsonWriter writer = gson.newJsonWriter(new FileWriter(path))) {
      gson.toJson(list, writer);
    } catch (NoSuchFileException e) {
      throw new ConfigurationException("Could not found extensions configuration!", e);
    } catch (IOException e) {
      throw new ConfigurationException("Could not save extensions configuration!", e);
    }
  }
}
