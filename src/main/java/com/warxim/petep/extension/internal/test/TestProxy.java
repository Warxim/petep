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
package com.warxim.petep.extension.internal.test;

import com.warxim.petep.core.connection.ConnectionManager;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.proxy.worker.Proxy;

public final class TestProxy extends Proxy {
  private final TestProxyConfig config;
  private final TestConnectionManager connectionManager;

  public TestProxy(TestProxyModule module, PetepHelper helper, TestProxyConfig config) {
    super(module, helper);
    this.config = config;
    this.connectionManager = new TestConnectionManager();
  }

  @Override
  public boolean prepare() {
    return true;
  }

  @Override
  public boolean start() {
    for (int i = 0; i < config.getNumberOfConnections(); ++i) {
      TestConnection connection = new TestConnection(connectionManager.nextId(), this);

      if (connection.start()) {
        connectionManager.add(connection);
      }
    }
    return true;
  }

  @Override
  public void stop() {
    connectionManager.stop();
  }

  @Override
  public ConnectionManager getConnectionManager() {
    return connectionManager;
  }

  public TestProxyConfig getConfig() {
    return config;
  }
}
