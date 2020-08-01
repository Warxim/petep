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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Group of rules. */
public class RuleGroup<R extends Rule> {
  private String code;
  private String name;

  private final CopyOnWriteArrayList<R> rules;

  /** Rule group constructor. */
  public RuleGroup(String code, String name) {
    this.code = code;
    this.name = name;
    rules = new CopyOnWriteArrayList<>();
  }

  public final String getCode() {
    return code;
  }

  public final String getName() {
    return name;
  }

  public final void setCode(String code) {
    this.code = code;
  }

  public final void setName(String code) {
    this.name = code;
  }

  public final List<R> getRules() {
    return rules;
  }

  public final int size() {
    return rules.size();
  }

  public final boolean addRule(R rule) {
    return rules.add(rule);
  }

  public final void setRule(int index, R rule) {
    rules.set(index, rule);
  }

  public final void replace(R oldRule, R newRule) {
    rules.set(rules.indexOf(oldRule), newRule);
  }

  public final boolean removeRule(R rule) {
    return rules.remove(rule);
  }

  public final void swapRules(int what, int with) {
    Collections.swap(rules, what, with);
  }

  public final int ruleCount() {
    return rules.size();
  }
}
