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

import java.util.List;
import com.warxim.petep.extension.PetepAPI;

/**
 * Abstract class of connection manager that could be used by internal or external modules to work
 * with connections of a given proxy.
 */
@PetepAPI
public abstract class ConnectionManager {
  /** Returns connection by ID. */
  public abstract Connection get(int id);

  /** Adds connection to the connection manager. */
  public abstract boolean add(Connection connection);

  /** Removes connection from the connection manager. */
  public abstract boolean remove(Connection connection);

  /** Removes connection from the connection manager. */
  public abstract Connection remove(int id);

  /** Returns list of connections. */
  public abstract List<Connection> getList();

  /** Stops all connections. */
  public abstract void stop();
}
