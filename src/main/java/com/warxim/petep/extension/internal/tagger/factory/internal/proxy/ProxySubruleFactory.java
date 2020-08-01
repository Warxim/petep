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
package com.warxim.petep.extension.internal.tagger.factory.internal.proxy;

import java.io.IOException;
import java.lang.reflect.Type;
import com.warxim.petep.extension.internal.tagger.factory.TagSubrule;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleConfigurator;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleData;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleFactory;
import com.warxim.petep.helper.ExtensionHelper;

public final class ProxySubruleFactory extends TagSubruleFactory {
  private ExtensionHelper helper;

  public ProxySubruleFactory(ExtensionHelper helper) {
    this.helper = helper;
  }

  @Override
  public String getCode() {
    return "proxy";
  }

  @Override
  public String getName() {
    return "Proxy ...";
  }

  @Override
  public TagSubrule createSubrule(TagSubruleData data) {
    return new ProxyTagSubrule(this, data);
  }

  @Override
  public TagSubruleConfigurator createConfigPane() throws IOException {
    return new ProxySubruleConfigurator(helper);
  }

  @Override
  public Type getConfigType() {
    return ProxyData.class;
  }
}
