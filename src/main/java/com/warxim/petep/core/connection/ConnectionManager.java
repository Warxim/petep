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

import com.warxim.petep.core.listener.ConnectionListener;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.helper.PetepHelper;

import java.util.List;
import java.util.Optional;

/**
 * Abstract class of connection manager that could be used by internal or external modules to work with connections of a given proxy.
 * <p>
 *     All methods should call appropriate listener methods, for example, when adding connection - onConnectionStart should be called.
 * </p>
 */
@PetepAPI
public abstract class ConnectionManager {
    /**
     * PETEP helper for currently running core.
     */
    protected final PetepHelper helper;

    /**
     * Connection listener for reporting new connections etc.
     */
    protected final ConnectionListener listener;

    /**
     * Constructs connection manager.
     * @param helper PETEP helper for currently running core
     */
    protected ConnectionManager(PetepHelper helper) {
        this.helper = helper;
        this.listener = helper.getConnectionListener();
    }

    /**
     * Obtains connection by code.
     * @param code Code of connection to be obtained
     * @return Connection or empty optional, if no connection exist with specified code
     */
    public abstract Optional<Connection> get(String code);

    /**
     * Adds connection to the connection manager.
     * @param connection Connection to be added
     * @return {@code true} if the connection was successfully added
     */
    public abstract boolean add(Connection connection);

    /**
     * Removes connection from the connection manager.
     * @param connection Connection to be removed
     * @return {@code true} if the connection was successfully removed (if it was present in the manager)
     */
    public abstract boolean remove(Connection connection);

    /**
     * Removes connection from the connection manager.
     * @param code Code of connection to be removed
     * @return Connection or empty optional, if no connection existed with specified code
     */
    public abstract Optional<Connection> remove(String code);

    /**
     * Get list of connections.
     * @return List of connections
     */
    public abstract List<Connection> getList();

    /**
     * Stops all connections.
     */
    public abstract void stop();
}
