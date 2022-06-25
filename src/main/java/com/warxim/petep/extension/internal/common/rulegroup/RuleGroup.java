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

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Group of rules.
 * @param <R> Rule type for this group
 */
@Getter
@Setter
public class RuleGroup<R extends Rule> implements AutoCloseable {
    private final List<R> rules;
    private String code;
    private String name;

    /**
     * Constructs rule group.
     * @param code Code of the group
     * @param name Name of the group
     */
    public RuleGroup(String code, String name) {
        this.code = code;
        this.name = name;
        rules = new CopyOnWriteArrayList<>();
    }

    /**
     * Get size of the rule group
     * @return Number of rules in this group
     */
    public final int size() {
        return rules.size();
    }

    /**
     * Adds rule to the group
     * @param rule Rule to be added
     * @return True if the rule was successfully added
     */
    public final boolean addRule(R rule) {
        return rules.add(rule);
    }

    /**
     * Adds rule to the group at given index
     * @param index Index on which to add the rule
     * @param rule Rule to be added
     */
    public final void addRule(int index, R rule) {
        rules.add(index, rule);
    }

    /**
     * Sets the rule on given index
     * @param index Index of the rule to replace
     * @param rule Rule to be set
     */
    public final void setRule(int index, R rule) {
        rules.set(index, rule);
    }

    /**
     * Replaces {@code oldRule} with {@code newRule}
     * @param oldRule Rule to be replaced
     * @param newRule Rule to be set
     */
    public final void replace(R oldRule, R newRule) {
        if (oldRule.equals(newRule)) {
            return;
        }
        var rule = rules.set(rules.indexOf(oldRule), newRule);
        tryCloseRule(rule);
    }

    /**
     * Removes rule from the group
     * @param rule Rule to be removed
     * @return True if the rule was successfully removed
     */
    public final boolean removeRule(R rule) {
        var removed = rules.remove(rule);
        if (removed) {
            tryCloseRule(rule);
        }
        return removed;
    }

    /**
     * Swaps rules at given indexes
     * @param what Index of rule
     * @param with Index of rule
     */
    public final void swapRules(int what, int with) {
        Collections.swap(rules, what, with);
    }

    @Override
    public void close() {
        for (var rule : rules) {
            tryCloseRule(rule);
        }
    }

    protected void tryCloseRule(Rule rule) {
        if (rule instanceof AutoCloseable) {
            try {
                ((AutoCloseable) rule).close();
            } catch (Exception e) {
                Logger.getGlobal().log(Level.SEVERE, "Could not close rule!", e);
            }
        }
    }
}
