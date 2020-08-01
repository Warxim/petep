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
package com.warxim.petep.extension.internal.http;

import java.util.logging.Logger;
import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.ExtensionInitListener;
import com.warxim.petep.extension.internal.http.modifier.remove_header.RemoveHeaderModifierFactory;
import com.warxim.petep.extension.internal.http.proxy.HttpProxyModuleFactory;
import com.warxim.petep.extension.internal.http.tagger.has_header.HasHeaderSubruleFactory;
import com.warxim.petep.extension.internal.http.tagger.is_http.IsHttpSubruleFactory;
import com.warxim.petep.extension.internal.http.tagger.is_websocket.IsWebSocketSubruleFactory;
import com.warxim.petep.extension.internal.modifier.ModifierApi;
import com.warxim.petep.extension.internal.tagger.TaggerApi;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.GuiHelper;

public final class HttpExtension extends Extension implements ExtensionInitListener {
  /** HTTP extension constructor. */
  public HttpExtension(String path) {
    super(path);

    Logger.getGlobal().info("HTTP extension loaded.");
  }

  /** Initializes HTTP extension (registers HTTP proxy module). */
  @Override
  public void init(ExtensionHelper helper) {
    helper.registerProxyModuleFactory(new HttpProxyModuleFactory(this));

    Logger.getGlobal().info("HTTP extension registered.");
  }

  @Override
  public void initGui(GuiHelper helper) {
    // No action needed.
  }

  @Override
  public String getCode() {
    return "http";
  }

  @Override
  public String getName() {
    return "HTTP extension";
  }

  @Override
  public String getDescription() {
    return "HTTP extension adds HTTP proxy to PETEP.";
  }

  @Override
  public String getVersion() {
    return "1.0";
  }

  @Override
  public void beforeInit(ExtensionHelper helper) {
    // Get TagSubruleModule registrator.
    Extension tagger = helper.getExtension("tagger");

    // Register HasHeader subrule module.
    if (tagger != null) {
      TaggerApi registrator = ((TaggerApi) tagger);
      if (!registrator.registerSubruleFactory(new HasHeaderSubruleFactory())) {
        Logger.getGlobal().info("HTTP Extension could not register HasHeader tag subrule.");
      }

      if (!registrator.registerSubruleFactory(new IsWebSocketSubruleFactory())) {
        Logger.getGlobal().info("HTTP Extension could not register IsWebsocket tag subrule.");
      }

      if (!registrator.registerSubruleFactory(new IsHttpSubruleFactory())) {
        Logger.getGlobal().info("HTTP Extension could not register IsHTTP tag subrule.");
      }
    }

    // Get ModifierModule registrator.
    Extension modifier = helper.getExtension("modifier");

    // Register RemoveHeader module.
    if (modifier != null && ((ModifierApi) modifier)
        .registerModifierFactory(new RemoveHeaderModifierFactory())) {
      Logger.getGlobal().info("HTTP Extension registered RemoveHeader modifier.");
    }
  }

  @Override
  public void afterInit(ExtensionHelper helper) {
    // No action needed.
  }
}
