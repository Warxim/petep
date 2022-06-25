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
package com.warxim.petep.extension.internal.common.rulegroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Manager of groups of rules.
 * @param <G> Group type for this manager
 */
public class RuleGroupManager<G extends RuleGroup<?>> implements AutoCloseable {
    private final ConcurrentHashMap<String, G> groups;

    /**
     * Constructs rule group manager.
     */
    public RuleGroupManager() {
        groups = new ConcurrentHashMap<>();
    }

    /**
     * Rule group manager constructor.
     * @param groups List of groups
     */
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

    /**
     * Checks if the manager manages group with given code
     * @param code Code to be checked
     * @return {@code true} if manager contains specified code.
     */
    public final boolean contains(String code) {
        return groups.containsKey(code);
    }

    /**
     * Obtains group for given code
     * @param code Code to find
     * @return Group for specified code or empty optional
     */
    public final Optional<G> get(String code) {
        return Optional.ofNullable(groups.get(code));
    }

    /**
     * Adds group to the manager if the group is not in it already (checks code)
     * @param group Group to add
     * @return Returns {@code true} if group was successfully added. Returns {@code false} if group code is reserved.
     */
    public final boolean add(G group) {
        return groups.putIfAbsent(group.getCode(), group) == null;
    }

    /**
     * Remove group by given code
     * @param code Code of group to remove
     */
    public final void remove(String code) {
        G group = groups.remove(code);
        if (group != null) {
            group.close();
        }
    }

    /**
     * Get list of groups.
     * @return List of groups
     */
    public final List<G> getList() {
        return new ArrayList<>(groups.values());
    }

    /**
     * Get map of groups mapped by code.
     * @return Map of groups mapped by code
     */
    public final Map<String, G> getMap() {
        return groups;
    }

    /**
     * Get number of groups managed by the manager
     * @return Number of groups
     */
    public final int size() {
        return groups.size();
    }

    @Override
    public void close() {
        for (var group : groups.values()) {
            group.close();
        }
    }
}
