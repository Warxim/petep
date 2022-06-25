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
package com.warxim.petep.extension.internal.modifier.config;

import com.google.gson.JsonElement;
import com.warxim.petep.extension.internal.modifier.factory.ModifierData;
import com.warxim.petep.util.GsonUtils;
import lombok.Getter;

/**
 * Configuration of modify rule.
 * <p>Contains information about modifier rule configuration.</p>
 */
@Getter
public final class ModifyRuleConfig {
    private final String name;
    private final String description;
    private final boolean enabled;
    private final String tag;
    /**
     * Code of factory, which created this rule. (Used for deserialization.)
     */
    private final String factoryCode;
    /**
     * Serialized data for deserialization into factory-specific type
     */
    private final JsonElement data;

    /**
     * Modify rule config constructor.
     * @param name Name of the rule
     * @param description Description of the rule
     * @param enabled {@code true} if the rule should be used
     * @param tag Tag to expect on PDUs for modification
     * @param factoryCode Code of factory that created this rule
     * @param data Data for the rule
     */
    public ModifyRuleConfig(String name,
                            String description,
                            boolean enabled,
                            String tag,
                            String factoryCode,
                            ModifierData data) {
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.tag = tag;
        this.factoryCode = factoryCode;

        this.data = GsonUtils.getGson().toJsonTree(data);
    }
}
