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
import com.warxim.petep.module.Module;
import com.warxim.petep.persistence.Configurable;
import com.warxim.petep.persistence.Storable;
import com.warxim.petep.util.ExtensionUtils;

/** Static class for saving modules. */
public final class ModulesSaver {
  private ModulesSaver() {}

  /** Saves modules to specified path. */
  public static <M extends Module<?>> void save(String path, Collection<M> modules)
      throws ConfigurationException {
    // Use gson with pretty printing.
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    JsonArray list = new JsonArray(modules.size());

    for (M module : modules) {
      JsonObject object = new JsonObject();

      // Add properties to the json object.
      object.addProperty(Constant.CONFIG_ITEM_CODE, module.getCode());
      object.addProperty(Constant.CONFIG_ITEM_NAME, module.getName());
      object.addProperty(Constant.CONFIG_ITEM_DESCRIPTION, module.getDescription());
      object.addProperty(Constant.CONFIG_ITEM_FACTORY, module.getFactory().getCode());
      object.addProperty(Constant.CONFIG_ITEM_ENABLED, module.isEnabled());

      // Add store to the json object.
      Type storeType = ExtensionUtils.getStoreType(module);
      if (storeType != null) {
        object.add(Constant.CONFIG_ITEM_STORE,
            gson.toJsonTree(((Storable<?>) module).saveStore(), storeType));
      }

      // Add config to the json object.
      Type configType = ExtensionUtils.getConfigType(module);
      if (configType != null) {
        object.add(Constant.CONFIG_ITEM_CONFIG,
            gson.toJsonTree(((Configurable<?>) module).saveConfig(), configType));
      }

      list.add(object);
    }

    // Write json to specified path.
    try (JsonWriter writer = gson.newJsonWriter(new FileWriter(path))) {
      gson.toJson(list, writer);
    } catch (NoSuchFileException e) {
      throw new ConfigurationException("Could not found configuration! (" + path + ")", e);
    } catch (IOException e) {
      throw new ConfigurationException("Could not save configuration! (" + path + ")", e);
    }
  }
}
