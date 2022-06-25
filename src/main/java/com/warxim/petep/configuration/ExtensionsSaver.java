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
import com.warxim.petep.extension.Extension;
import com.warxim.petep.persistence.Configurable;
import com.warxim.petep.persistence.Storable;
import com.warxim.petep.util.ExtensionUtils;
import com.warxim.petep.util.GsonUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Collection;

/**
 * Static class for saving extensions.
 */
public final class ExtensionsSaver {
    private ExtensionsSaver() {
    }

    /**
     * Saves extensions to specified path.
     * @param path Path to extensions.json configuration file
     * @param extensions Extensions to be saved into the configuration file
     * @throws ConfigurationException If the extensions could not be saved
     */
    public static void save(String path, Collection<Extension> extensions)
            throws ConfigurationException {
        // Enable pretty printing.
        var gson = GsonUtils.getGson();
        var list = new JsonArray(extensions.size());

        for (var extension : extensions) {
            var object = new JsonObject();

            // Save extension path.
            object.addProperty(Constant.CONFIG_ITEM_PATH, extension.getPath());

            // Save extension store.
            var maybeStoreType = ExtensionUtils.getStoreType(extension);
            if (maybeStoreType.isPresent()) {
                object.add(Constant.CONFIG_ITEM_STORE, gson.toJsonTree(((Storable<?>) extension).saveStore(), maybeStoreType.get()));
            }

            // Save extension config.
            var maybeConfigType = ExtensionUtils.getConfigType(extension);
            if (maybeConfigType.isPresent()) {
                object.add(Constant.CONFIG_ITEM_CONFIG, gson.toJsonTree(((Configurable<?>) extension).saveConfig(), maybeConfigType.get()));
            }

            list.add(object);
        }

        // Write json to specified path.
        try (var writer = gson.newJsonWriter(new FileWriter(path, Constant.FILE_CHARSET))) {
            gson.toJson(list, writer);
        } catch (NoSuchFileException e) {
            throw new ConfigurationException("Could not found extensions configuration!", e);
        } catch (IOException e) {
            throw new ConfigurationException("Could not save extensions configuration!", e);
        }
    }
}
