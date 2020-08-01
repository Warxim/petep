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
package com.warxim.petep.extension.internal.tcp;

import java.util.logging.Logger;
import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.internal.tcp.proxy.TcpProxyModuleFactory;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.GuiHelper;

/** TCP extension. */
public final class TcpExtension extends Extension {
  /** TCP extension constructor. */
  public TcpExtension(String path) {
    super(path);

    Logger.getGlobal().info("TCP extension loaded.");
  }

  /** Initializes TCP extension (registers TCP proxy module). */
  @Override
  public void init(ExtensionHelper helper) {
    helper.registerProxyModuleFactory(new TcpProxyModuleFactory(this));

    Logger.getGlobal().info("TCP extension registered.");
  }

  @Override
  public void initGui(GuiHelper helper) {
    // No action needed.
  }

  @Override
  public String getCode() {
    return "tcp";
  }

  @Override
  public String getName() {
    return "TCP extension";
  }

  @Override
  public String getDescription() {
    return "TCP extension adds TCP proxy to PETEP.";
  }

  @Override
  public String getVersion() {
    return "1.0";
  }
}
