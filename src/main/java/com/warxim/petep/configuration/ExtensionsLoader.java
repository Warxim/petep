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

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.warxim.petep.common.Constant;
import com.warxim.petep.exception.ConfigurationException;
import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.internal.catcher.CatcherExtension;
import com.warxim.petep.extension.internal.connectionview.ConnectionViewExtension;
import com.warxim.petep.extension.internal.deluder.DeluderExtension;
import com.warxim.petep.extension.internal.externalhttpproxy.EHTTPPExtension;
import com.warxim.petep.extension.internal.history.HistoryExtension;
import com.warxim.petep.extension.internal.http.HttpExtension;
import com.warxim.petep.extension.internal.logger.LoggerExtension;
import com.warxim.petep.extension.internal.modifier.ModifierExtension;
import com.warxim.petep.extension.internal.repeater.RepeaterExtension;
import com.warxim.petep.extension.internal.scripter.ScripterExtension;
import com.warxim.petep.extension.internal.tagger.TaggerExtension;
import com.warxim.petep.extension.internal.tcp.TcpExtension;
import com.warxim.petep.extension.internal.udp.UdpExtension;
import com.warxim.petep.persistence.Configurable;
import com.warxim.petep.persistence.Storable;
import com.warxim.petep.util.ExtensionUtils;
import com.warxim.petep.util.FileUtils;
import com.warxim.petep.util.GsonUtils;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;

/**
 * Static class for loading extensions.
 */
public final class ExtensionsLoader {
    private ExtensionsLoader() {
    }

    /**
     * Loads extensions from specified path.
     * @param path Path to extensions.json configuration file
     * @return List of loaded extensions
     * @throws ConfigurationException If the extensions could not be loaded
     */
    public static List<Extension> load(String path) throws ConfigurationException {
        // Read configuration from specified path.
        try (var reader = new JsonReader(new FileReader(path, Constant.FILE_CHARSET))) {
            var list = JsonParser.parseReader(reader).getAsJsonArray();
            var extensions = new ArrayList<Extension>(list.size());

            for (var i = 0; i < list.size(); ++i) {
                var extension = list.get(i).getAsJsonObject();

                // Load extension and add it to list.
                extensions.add(loadExtension(extension.get(Constant.CONFIG_ITEM_PATH).getAsString(),
                        extension.get(Constant.CONFIG_ITEM_STORE),
                        extension.get(Constant.CONFIG_ITEM_CONFIG)));
            }

            return extensions;
        } catch (JsonParseException e) {
            throw new ConfigurationException("Could not parse project configuration!", e);
        } catch (NoSuchFileException e) {
            throw new ConfigurationException("Could not found project configuration!", e);
        } catch (IOException e) {
            throw new ConfigurationException("Could not load project configuration!", e);
        }
    }

    /**
     * Loads specified extension by given name (internal or external) and hands over the configuration.
     */
    private static Extension loadExtension(String path, JsonElement store, JsonElement config)
            throws ConfigurationException {
        Extension extension;

        switch (path) {
            case "logger":
                extension = new LoggerExtension("logger");
                break;
            case "external_http_proxy":
                extension = new EHTTPPExtension("external_http_proxy");
                break;
            case "tcp":
                extension = new TcpExtension("tcp");
                break;
            case "udp":
                extension = new UdpExtension("udp");
                break;
            case "tagger":
                extension = new TaggerExtension("tagger");
                break;
            case "catcher":
                extension = new CatcherExtension("catcher");
                break;
            case "connection_view":
                extension = new ConnectionViewExtension("connection_view");
                break;
            case "http":
                extension = new HttpExtension("http");
                break;
            case "modifier":
                extension = new ModifierExtension("modifier");
                break;
            case "history":
                extension = new HistoryExtension("history");
                break;
            case "scripter":
                extension = new ScripterExtension("scripter");
                break;
            case "repeater":
                extension = new RepeaterExtension("repeater");
                break;
            case "deluder":
                extension = new DeluderExtension("deluder");
                break;
            default:
                extension = loadExternalExtension(path);
        }

        try {
            loadExtensionConfig(extension, config);
            loadExtensionStore(extension, store);
        } catch (RuntimeException e) {
            throw new ConfigurationException("Extension '" + path + "' could not be loaded!", e);
        }

        return extension;
    }

    /**
     * Decides if extension expects store and hands it over.
     */
    private static void loadExtensionStore(Extension extension, JsonElement store) {
        // No store in json.
        if (store == null) {
            return;
        }

        // Get store type using reflections.
        var maybeStoreType = ExtensionUtils.getStoreType(extension);
        if (maybeStoreType.isEmpty()) {
            return;
        }

        // Deserialize store to expected type and give the to the extension.
        ((Storable<?>) extension).loadStore(GsonUtils.getGson().fromJson(store, maybeStoreType.get()));
    }

    /**
     * Decides if extension expects config and hands it over.
     */
    private static void loadExtensionConfig(Extension extension, JsonElement config) {
        // No config in json.
        if (config == null) {
            return;
        }

        // Get config type using reflections.
        var maybeConfigType = ExtensionUtils.getConfigType(extension);
        if (maybeConfigType.isEmpty()) {
            return;
        }

        // Deserialize config to expected type and give the config to the extension.
        ((Configurable<?>) extension).loadConfig(GsonUtils.getGson().fromJson(config, maybeConfigType.get()));
    }

    /**
     * Loads specified .jar file extension.
     * @throws ConfigurationException if the extension could not be loaded
     */
    private static Extension loadExternalExtension(String path) throws ConfigurationException {
        try {
            var classLoader = new URLClassLoader(new URL[]{FileUtils.getApplicationFile(path).toURI().toURL()});

            // Create instance of extension class.
            return (Extension) Class.forName("petep.PetepExtension", true, classLoader)
                    .getConstructor(String.class)
                    .newInstance(path);
        } catch (NoSuchMethodException
                | IllegalAccessException
                | InstantiationException
                | InvocationTargetException
                | ClassNotFoundException
                | MalformedURLException e) {
            throw new ConfigurationException("Extension '" + path + "' could not be loaded!", e);
        }
    }
}
