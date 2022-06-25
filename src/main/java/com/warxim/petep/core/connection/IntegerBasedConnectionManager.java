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

import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.helper.PetepHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Default connection manager that uses ConcurrentHashMap for connection storage and Integer as connection code.
 * <p>
 *     Does not call start/stop on connection, but calls listener onStart/onStop, when connection is added/removed.
 * </p>
 */
@PetepAPI
public class IntegerBasedConnectionManager extends ConnectionManager {
    /**
     * Map of connections.
     */
    protected final ConcurrentHashMap<Integer, Connection> connections;

    /**
     * ID of last connection.
     */
    private final AtomicInteger lastId;

    /**
     * Constructs connection manager based on integer codes.
     * @param helper PETEP helper for currently running core
     */
    public IntegerBasedConnectionManager(PetepHelper helper) {
        super(helper);
        connections = new ConcurrentHashMap<>();
        lastId = new AtomicInteger(0);
    }

    @Override
    public Optional<Connection> get(String code) {
        return Optional.ofNullable(connections.get(Integer.parseInt(code)));
    }

    /**
     * {@inheritDoc}
     * <p>
     *  <b>Note:</b> Does not start the connection, but reports it as started to listener.
     * </p>
     * @param connection Connection to be added
     * @return {@code true} if the connection was successfully added
     */
    @Override
    public boolean add(Connection connection) {
        if (connections.putIfAbsent(Integer.parseInt(connection.getCode()), connection) == null) {
            listener.onConnectionStart(connection);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     *  <b>Note:</b> Does not stop the connection, but reports it as stopped to listener.
     * </p>
     * @param connection Connection to be removed
     * @return {@code true} if the connection was successfully removed (if it was present in the manager)
     */
    @Override
    public boolean remove(Connection connection) {
        if (connections.remove(Integer.parseInt(connection.getCode()), connection)) {
            listener.onConnectionStop(connection);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     *  <b>Note:</b> Does not stop the connection, but reports it as stopped to listener.
     * </p>
     * @param code Code of connection to be removed (automatically converted to integer)
     * @return Connection or empty optional, if no connection existed with specified code
     */
    @Override
    public Optional<Connection> remove(String code) {
        var connection = connections.remove(Integer.parseInt(code));
        if (connection != null) {
            listener.onConnectionStop(connection);
        }
        return Optional.ofNullable(connection);
    }

    @Override
    public List<Connection> getList() {
        return new ArrayList<>(connections.values());
    }

    @Override
    public void stop() {
        connections.values().parallelStream().forEach(Connection::stop);
    }

    /**
     * Generates new id for connection.
     * @return (Last ID + 1) converted to string.
     */
    public String nextCode() {
        return String.valueOf(lastId.incrementAndGet());
    }
}
