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

/** Tag rule configuration. */
public final class TagRuleConfig {
  private final String name;
  private final String description;
  private final boolean enabled;
  private final String tag;
  private final String expressionString;
  private final List<TagSubruleConfig> subrules;

  /** Tag rule configuration constructor. */
  public TagRuleConfig(
      String name,
      String description,
      boolean enabled,
      String tag,
      List<TagSubruleConfig> subrules,
      String expressionString) {
    this.name = name;
    this.description = description;
    this.enabled = enabled;
    this.tag = tag;
    this.subrules = subrules;
    this.expressionString = expressionString;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public String getTag() {
    return tag;
  }

  public List<TagSubruleConfig> getSubrules() {
    return subrules;
  }

  public String getExpressionString() {
    return expressionString;
  }
}
