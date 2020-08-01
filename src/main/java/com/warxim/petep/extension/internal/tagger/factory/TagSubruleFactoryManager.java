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
package com.warxim.petep.extension.internal.tagger.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Tag subrule factory manager. */
public final class TagSubruleFactoryManager {
  private final Map<String, TagSubruleFactory> factories;

  /** Tag subrule factory manager constructor. */
  public TagSubruleFactoryManager() {
    factories = new ConcurrentHashMap<>();
  }

  public boolean registerFactory(TagSubruleFactory factory) {
    return factories.putIfAbsent(factory.getCode(), factory) == null;
  }

  public TagSubruleFactory getFactory(String code) {
    return factories.get(code);
  }

  public List<TagSubruleFactory> getFactories() {
    return new ArrayList<>(factories.values());
  }
}
