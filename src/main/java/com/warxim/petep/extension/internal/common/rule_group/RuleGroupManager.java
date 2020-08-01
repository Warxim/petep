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
package com.warxim.petep.extension.internal.common.rule_group;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/** Manager of groups of rules. */
public class RuleGroupManager<G extends RuleGroup<?>> {
  private final ConcurrentHashMap<String, G> groups;

  /** Rule group manager constructor. */
  public RuleGroupManager() {
    groups = new ConcurrentHashMap<>();
  }

  /** Rule group manager constructor. */
  public RuleGroupManager(List<G> groups) {
    this();
    for (G group : groups) {
      if (!add(group)) {
        Logger.getGlobal()
            .severe("Rule group " + group.getCode()
                + " was not loaded, because group with this code already exists!");
      }
    }
  }

  /** Returns true if manager contains specified code. */
  public final boolean contains(String code) {
    return groups.containsKey(code);
  }

  /** Returns group for specified code. */
  public final G get(String code) {
    return groups.get(code);
  }

  /** Returns true if group was successfully added. Returns false if group code is reserved. */
  public final boolean add(G group) {
    return groups.putIfAbsent(group.getCode(), group) == null;
  }

  /** Removes group by its code. */
  public final void remove(String code) {
    groups.remove(code);
  }

  /** Returns list of groups. */
  public final List<G> getList() {
    return new ArrayList<>(groups.values());
  }

  /** Returns map of groups. */
  public final Map<String, G> getMap() {
    return groups;
  }

  /** Returns number of groups. */
  public final int size() {
    return groups.size();
  }
}
