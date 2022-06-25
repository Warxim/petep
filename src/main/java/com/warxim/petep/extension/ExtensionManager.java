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

import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.GuiHelper;

import java.util.*;

/**
 * Extension manager.
 */
public final class ExtensionManager {
    /**
     * Map of extensions (key = extension code).
     */
    private final Map<String, Extension> extensionMap;

    /**
     * List of extensions.
     */
    private final List<Extension> extensionList;

    /**
     * Extension manager constructor that takes list of extensions and stores them to own unmodifiable collections.
     * @param extensions List of loaded extensions
     */
    public ExtensionManager(List<Extension> extensions) {
        // Make list unmodifiable.
        extensionList = Collections.unmodifiableList(extensions);

        // Fill map with extensions with code as key.
        var map = new HashMap<String, Extension>((int) (extensions.size() / 0.75 + 1), 0.75f);
        for (var extension : extensions) {
            map.put(extension.getCode(), extension);
        }

        // Make map unmodifiable
        extensionMap = Collections.unmodifiableMap(map);
    }

    /**
     * Obtains unmodifiable extensions map.
     * @return Extensions mapped by their code
     */
    public Map<String, Extension> getMap() {
        return extensionMap;
    }

    /**
     * Obtains unmodifiable extensions list.
     * @return List of loaded extension
     */
    public List<Extension> getList() {
        return extensionList;
    }

    /**
     * Obtains extension by code.
     * @param code Code of the extension
     * @return Extension or empty optional if it does not exist
     */
    public Optional<Extension> getExtension(String code) {
        return Optional.ofNullable(extensionMap.get(code));
    }

    /**
     * Inits extensions (alls before, init and after methods).
     * @param helper Helper for init methods
     */
    public void init(ExtensionHelper helper) {
        extensionList.parallelStream().forEach(extension -> extension.beforeInit(helper));
        extensionList.parallelStream().forEach(extension -> extension.init(helper));
        extensionList.parallelStream().forEach(extension -> extension.afterInit(helper));
    }

    /**
     * Inits extensions GUI (alls before, init and after methods).
     * @param helper Helper for init methods
     */
    public void initGui(GuiHelper helper) {
        extensionList.parallelStream().forEach(extension -> extension.beforeInitGui(helper));
        extensionList.parallelStream().forEach(extension -> extension.initGui(helper));
        extensionList.parallelStream().forEach(extension -> extension.afterInitGui(helper));
    }

    /**
     * Destroys extensions.
     */
    public void destroy() {
        extensionList.parallelStream().forEach(Extension::destroy);
    }
}
