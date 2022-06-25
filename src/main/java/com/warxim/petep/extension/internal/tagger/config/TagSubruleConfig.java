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
package com.warxim.petep.extension.internal.tagger.config;

import com.google.gson.JsonElement;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleData;
import com.warxim.petep.util.GsonUtils;
import lombok.Getter;

/**
 * Configuration of tag subrules.
 */
@Getter
public final class TagSubruleConfig {
    /**
     * Code of factory that generated this subrule
     */
    private final String factoryCode;
    /**
     * Serialized data of tag subrule
     */
    private final JsonElement data;

    /**
     * Constructs tag subrule config.
     * @param factoryCode Code of factory that created this subrule
     * @param data Data for the subrule
     */
    public TagSubruleConfig(String factoryCode, TagSubruleData data) {
        this.factoryCode = factoryCode;
        this.data = GsonUtils.getGson().toJsonTree(data);
    }
}
