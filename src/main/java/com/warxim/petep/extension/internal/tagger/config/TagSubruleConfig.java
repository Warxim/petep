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

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleData;

/** Configuration of tag subrules. */
public final class TagSubruleConfig {
  private final String factoryCode;
  private final JsonElement data;

  /** Tag subrule config constructor. */
  public TagSubruleConfig(String factoryCode, TagSubruleData data) {
    this.factoryCode = factoryCode;
    this.data = new GsonBuilder().setPrettyPrinting().create().toJsonTree(data);
  }

  public String getFactoryCode() {
    return factoryCode;
  }

  public JsonElement getData() {
    return data;
  }
}
