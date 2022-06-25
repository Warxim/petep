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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.warxim.petep.common.Constant;
import com.warxim.petep.exception.ConfigurationException;
import com.warxim.petep.module.Module;
import com.warxim.petep.persistence.Configurable;
import com.warxim.petep.persistence.Storable;
import com.warxim.petep.util.ExtensionUtils;
import com.warxim.petep.util.GsonUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Collection;

/**
 * Static class for saving modules.
 */
public final class ModulesSaver {
    private ModulesSaver() {
    }

    /**
     * Saves modules to specified path.
     * @param path Path to modules configuration file
     * @param modules Modules to be saved into the configuration file
     * @throws ConfigurationException If the modules could not be saved
     */
    public static <M extends Module<?>> void save(String path, Collection<M> modules)
            throws ConfigurationException {
        // Use gson with pretty printing.
        var gson = GsonUtils.getGson();
        var list = new JsonArray(modules.size());

        for (var module : modules) {
            var object = new JsonObject();

            // Add properties to the json object.
            object.addProperty(Constant.CONFIG_ITEM_CODE, module.getCode());
            object.addProperty(Constant.CONFIG_ITEM_NAME, module.getName());
            object.addProperty(Constant.CONFIG_ITEM_DESCRIPTION, module.getDescription());
            object.addProperty(Constant.CONFIG_ITEM_FACTORY, module.getFactory().getCode());
            object.addProperty(Constant.CONFIG_ITEM_ENABLED, module.isEnabled());

            // Add store to the json object.
            var maybeStoreType = ExtensionUtils.getStoreType(module);
            if (maybeStoreType.isPresent()) {
                object.add(Constant.CONFIG_ITEM_STORE, gson.toJsonTree(((Storable<?>) module).saveStore(), maybeStoreType.get()));
            }

            // Add config to the json object.
            var maybeConfigType = ExtensionUtils.getConfigType(module);
            if (maybeConfigType.isPresent()) {
                object.add(Constant.CONFIG_ITEM_CONFIG, gson.toJsonTree(((Configurable<?>) module).saveConfig(), maybeConfigType.get()));
            }

            list.add(object);
        }

        // Write json to specified path.
        try (var writer = gson.newJsonWriter(new FileWriter(path, Constant.FILE_CHARSET))) {
            gson.toJson(list, writer);
        } catch (NoSuchFileException e) {
            throw new ConfigurationException("Could not found configuration! (" + path + ")", e);
        } catch (IOException e) {
            throw new ConfigurationException("Could not save configuration! (" + path + ")", e);
        }
    }
}
