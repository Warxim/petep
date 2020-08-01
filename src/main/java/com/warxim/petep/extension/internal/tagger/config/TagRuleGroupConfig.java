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

import java.util.List;

/** Configuration of group of tag rules. */
public final class TagRuleGroupConfig {
  private final String code;
  private final String name;
  private final List<TagRuleConfig> rules;

  /** Tag rule group config. */
  public TagRuleGroupConfig(String code, String name, List<TagRuleConfig> rules) {
    this.code = code;
    this.name = name;
    this.rules = rules;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public List<TagRuleConfig> getRules() {
    return rules;
  }
}
