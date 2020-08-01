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
package com.warxim.petep.core.connection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import com.warxim.petep.extension.PetepAPI;

/** Default connection manager that uses ConcurrentHashMap for connection storage. */
@PetepAPI
public class DefaultConnectionManager extends ConnectionManager {
  /** Map of connections. */
  protected final ConcurrentHashMap<Integer, Connection> connections;

  /** ID of last connection. */
  private int lastId;

  public DefaultConnectionManager() {
    connections = new ConcurrentHashMap<>();
    lastId = 0;
  }

  @Override
  public Connection get(int id) {
    return connections.get(id);
  }

  @Override
  public boolean add(Connection connection) {
    return connections.putIfAbsent(connection.getId(), connection) == null;
  }

  @Override
  public boolean remove(Connection connection) {
    return connections.remove(connection.getId(), connection);
  }

  @Override
  public Connection remove(int id) {
    return connections.remove(id);
  }

  @Override
  public List<Connection> getList() {
    return new ArrayList<>(connections.values());
  }

  @Override
  public void stop() {
    connections.values().parallelStream().forEach(Connection::stop);
  }

  /** Generates new id for connection. */
  public synchronized int nextId() {
    return lastId++;
  }
}
