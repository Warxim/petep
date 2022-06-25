/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2021 Michal Válka
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
package com.warxim.petep.extension.internal.scripter.config;

import com.google.gson.JsonElement;
import com.warxim.petep.extension.internal.scripter.rule.ScriptType;
import com.warxim.petep.util.GsonUtils;
import lombok.Getter;

/**
 * Script configuration
 */
@Getter
public final class ScriptConfig {
    private final String name;
    private final String description;
    private final String language;
    private final boolean enabled;
    private final ScriptType type;
    private final JsonElement data;

    /**
     * Constructs script configuration.
     * @param name Name of the script
     * @param description Description of the script
     * @param enabled {@code true} if the script should be used
     * @param language Language of the script
     * @param type Type of the script
     * @param data Data describing the script
     */
    public ScriptConfig(String name,
                        String description,
                        boolean enabled,
                        String language,
                        ScriptType type,
                        ScriptData data) {
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.language = language;
        this.type = type;
        this.data = GsonUtils.getGson().toJsonTree(data);
    }
}
