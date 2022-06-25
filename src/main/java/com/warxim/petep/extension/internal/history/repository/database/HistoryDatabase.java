/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2021 Michal Válka
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
package com.warxim.petep.extension.internal.history.repository.database;

import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * History database
 * <p>Initializes database file and creates required tables.</p>
 */
@Getter
public class HistoryDatabase implements AutoCloseable {
    private Connection connection;

    /**
     * Constructs history database with given url.
     * @param url Path to database file
     * @throws SQLException If anything fails during history database processing
     */
    public HistoryDatabase(String url) throws SQLException {
        connection = DriverManager.getConnection(url);

        init();
    }

    /**
     * Creates all history tables.
     */
    private void init() throws SQLException {
        try (var stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
            stmt.execute("PRAGMA synchronous = OFF;");
            stmt.execute("PRAGMA journal_mode = WAL;");
            stmt.execute("PRAGMA locking_mode = EXCLUSIVE;");

            stmt.execute("CREATE TABLE IF NOT EXISTS proxy ("
                    + "id INTEGER PRIMARY KEY,"
                    + "code TEXT NOT NULL,"
                    + "name TEXT NOT NULL"
                    + ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS interceptor ("
                    + "id INTEGER PRIMARY KEY,"
                    + "code TEXT NOT NULL,"
                    + "name TEXT NOT NULL"
                    + ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS connection ("
                    + "id INTEGER PRIMARY KEY,"
                    + "code TEXT NOT NULL,"
                    + "name TEXT NOT NULL"
                    + ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS charset ("
                    + "id INTEGER PRIMARY KEY,"
                    + "code TEXT NOT NULL UNIQUE"
                    + ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS tag ("
                    + "id INTEGER PRIMARY KEY,"
                    + "code TEXT NOT NULL UNIQUE"
                    + ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS metadata ("
                    + "id INTEGER PRIMARY KEY,"
                    + "code TEXT NOT NULL UNIQUE"
                    + ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS pdu ("
                    + "id INTEGER PRIMARY KEY,"
                    + "id_proxy INTEGER NOT NULL,"
                    + "id_connection INTEGER NOT NULL,"
                    + "id_interceptor INTEGER NOT NULL,"
                    + "id_charset INTEGER NOT NULL,"
                    + "destination INTEGER NOT NULL,"
                    + "size INTEGER NOT NULL,"
                    + "time INTEGER NOT NULL,"
                    + "data BLOB NOT NULL,"
                    + "FOREIGN KEY(id_proxy) REFERENCES proxy(id),"
                    + "FOREIGN KEY(id_connection) REFERENCES connection(id),"
                    + "FOREIGN KEY(id_interceptor) REFERENCES interceptor(id),"
                    + "FOREIGN KEY(id_charset) REFERENCES charset(id)"
                    + ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS pdu_has_metadata ("
                    + "id_pdu INTEGER,"
                    + "id_metadata INTEGER,"
                    + "value TEXT,"
                    + "PRIMARY KEY(id_pdu, id_metadata),"
                    + "FOREIGN KEY(id_pdu) REFERENCES pdu(id) ON DELETE CASCADE,"
                    + "FOREIGN KEY(id_metadata) REFERENCES metadata(id) ON DELETE CASCADE"
                    + ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS pdu_has_tag ("
                    + "id_pdu INTEGER,"
                    + "id_tag INTEGER,"
                    + "PRIMARY KEY(id_pdu, id_tag),"
                    + "FOREIGN KEY(id_pdu) REFERENCES pdu(id) ON DELETE CASCADE,"
                    + "FOREIGN KEY(id_tag) REFERENCES tag(id) ON DELETE CASCADE"
                    + ");");
        }
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                Logger.getGlobal().log(Level.SEVERE, "Could not close database connection!", e);
            }
        }
    }
}
