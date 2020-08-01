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

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.warxim.petep.common.Constant;
import com.warxim.petep.exception.ConfigurationException;
import com.warxim.petep.exception.ExtensionLoadException;
import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.internal.catcher.CatcherExtension;
import com.warxim.petep.extension.internal.connection_view.ConnectionViewExtension;
import com.warxim.petep.extension.internal.external_http_proxy.EHTTPPExtension;
import com.warxim.petep.extension.internal.http.HttpExtension;
import com.warxim.petep.extension.internal.logger.LoggerExtension;
import com.warxim.petep.extension.internal.modifier.ModifierExtension;
import com.warxim.petep.extension.internal.tagger.TaggerExtension;
import com.warxim.petep.extension.internal.tcp.TcpExtension;
import com.warxim.petep.extension.internal.test.TestExtension;
import com.warxim.petep.persistence.Configurable;
import com.warxim.petep.persistence.Storable;
import com.warxim.petep.util.ExtensionUtils;
import com.warxim.petep.util.FileUtils;

/** Static class for loading extensions. */
public final class ExtensionsLoader {
  private ExtensionsLoader() {}

  /** Loads extensions from specified path. */
  public static List<Extension> load(String path) throws ConfigurationException {
    // Read configuration from specified path.
    try (JsonReader reader = new JsonReader(new FileReader(path))) {
      JsonArray list = JsonParser.parseReader(reader).getAsJsonArray();
      ArrayList<Extension> extensions = new ArrayList<>(list.size());

      for (int i = 0; i < list.size(); ++i) {
        try {
          JsonObject extension = list.get(i).getAsJsonObject();

          // Load extension and add it to list.
          extensions.add(loadExtension(extension.get(Constant.CONFIG_ITEM_PATH).getAsString(),
              extension.get(Constant.CONFIG_ITEM_STORE),
              extension.get(Constant.CONFIG_ITEM_CONFIG)));
        } catch (ExtensionLoadException e) {
          throw new ConfigurationException("Could not load extension!", e);
        }
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
   * Loads specified extension by given name (internal or external) and hands over the
   * configuration.
   */
  private static Extension loadExtension(String path, JsonElement store, JsonElement config)
      throws ExtensionLoadException {
    Extension extension;

    switch (path) {
      case "logger":
        extension = new LoggerExtension("logger");
        break;
      case "test":
        extension = new TestExtension("test");
        break;
      case "external_http_proxy":
        extension = new EHTTPPExtension("external_http_proxy");
        break;
      case "tcp":
        extension = new TcpExtension("tcp");
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
      default:
        extension = loadExternalExtension(path);
    }

    loadExtensionConfig(extension, config);
    loadExtensionStore(extension, store);

    return extension;
  }

  /**
   * Decides if extension expects store and hands it over.
   *
   * @throws ExtensionLoadException
   */
  private static void loadExtensionStore(Extension extension, JsonElement store) {
    // No store in json.
    if (store == null) {
      return;
    }

    // Get store type using reflections.
    Type storeType = ExtensionUtils.getStoreType(extension);
    if (storeType == null) {
      return;
    }

    // Deserialize store to expected type and give the to the extension.
    ((Storable<?>) extension).loadStore(new GsonBuilder().create().fromJson(store, storeType));
  }

  /**
   * Decides if extension expects config and hands it over.
   *
   * @throws ExtensionLoadException
   */
  private static void loadExtensionConfig(Extension extension, JsonElement config) {
    // No config in json.
    if (config == null) {
      return;
    }

    // Get config type using reflections.
    Type configType = ExtensionUtils.getConfigType(extension);
    if (configType == null) {
      return;
    }

    // Deserialize config to expected type and give the config to the extension.
    ((Configurable<?>) extension)
        .loadConfig(new GsonBuilder().create().fromJson(config, configType));
  }

  /** Loads specified .jar file extension. */
  private static Extension loadExternalExtension(String path) throws ExtensionLoadException {
    try {
      URLClassLoader classLoader =
          new URLClassLoader(new URL[] {FileUtils.getApplicationFile(path).toURI().toURL()});

      // Create instance of extension class.
      return (Extension) Class.forName("petep.PetepExtension", true, classLoader)
          .getConstructor(String.class)
          .newInstance(path);
    } catch (MalformedURLException | InstantiationException | IllegalAccessException
        | ClassNotFoundException | IllegalArgumentException | InvocationTargetException
        | NoSuchMethodException | SecurityException e) {
      throw new ExtensionLoadException(
          "Extension '" + path + "' could not be loaded -> " + e.toString(), e);
    }
  }
}
