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
package com.warxim.petep.extension;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.GuiHelper;

/** Extension manager. */
public final class ExtensionManager {
  /** Map of extensions (key = extension code). */
  private final Map<String, Extension> extensionMap;

  /** List of extensions. */
  private final List<Extension> extensionList;

  /**
   * Extension manager constructor that takes list of extensions and stores them to own unmodifiable
   * collections.
   */
  public ExtensionManager(List<Extension> extensions) {
    // Make list unmodifiable.
    extensionList = Collections.unmodifiableList(extensions);

    // Fill map with extensions with code as key.
    Map<String, Extension> map = new HashMap<>((int) (extensions.size() / 0.75 + 1), 0.75f);
    for (Extension extension : extensions) {
      map.put(extension.getCode(), extension);
    }

    // Make map unmodifiable
    extensionMap = Collections.unmodifiableMap(map);
  }

  /** Returns unmodifiable extensions map <String(ExtensionName), Extension>. */
  public Map<String, Extension> getMap() {
    return extensionMap;
  }

  /** Returns unmodifiable extensions list. */
  public List<Extension> getList() {
    return extensionList;
  }

  /** Returns extension by code. */
  public Extension getExtension(String code) {
    return extensionMap.get(code);
  }

  /** Inits extensions. */
  public void init(ExtensionHelper helper) {
    // Call beforeInit on listeners.
    extensionList.stream()
        .filter(ExtensionInitListener.class::isInstance)
        .map(ExtensionInitListener.class::cast)
        .forEach(extension -> extension.beforeInit(helper));

    extensionList.parallelStream().forEach(extension -> extension.init(helper));

    // Call afterInit on listeners.
    extensionList.stream()
        .filter(ExtensionInitListener.class::isInstance)
        .map(ExtensionInitListener.class::cast)
        .forEach(extension -> extension.afterInit(helper));
  }

  /** Inits extensions GUI. */
  public void initGui(GuiHelper helper) {
    // Call beforeInitGui on listeners.
    extensionList.stream()
        .filter(ExtensionGuiInitListener.class::isInstance)
        .map(ExtensionGuiInitListener.class::cast)
        .forEach(extension -> extension.beforeInitGui(helper));

    extensionList.parallelStream().forEach(extension -> extension.initGui(helper));

    // Call afterInitGui on listeners.
    extensionList.stream()
        .filter(ExtensionGuiInitListener.class::isInstance)
        .map(ExtensionGuiInitListener.class::cast)
        .forEach(extension -> extension.afterInitGui(helper));
  }
}
