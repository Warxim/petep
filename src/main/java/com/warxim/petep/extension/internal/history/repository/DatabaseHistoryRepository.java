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
package com.warxim.petep.extension.internal.history.repository;

import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.history.model.*;
import com.warxim.petep.extension.internal.history.repository.database.HistoryDatabase;

import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.warxim.petep.extension.internal.history.util.HistoryDatabaseUtils.*;
import static com.warxim.petep.extension.internal.history.repository.database.DatabaseHistoryQuery.*;

/**
 * Default history repository implementation using SQLite database.
 * <p>Creates and uses SQLite database for storing and querying history.</p>
 */
public class DatabaseHistoryRepository implements HistoryRepository {
    /*
     * Tables
     */
    private static final String TABLE_PROXY = "proxy";
    private static final String TABLE_TAG = "tag";
    private static final String TABLE_CONNECTION = "connection";
    private static final String TABLE_INTERCEPTOR = "interceptor";
    private static final String TABLE_METADATA = "metadata";
    private static final String TABLE_CHARSET = "charset";

    /*
     * Statements
     */
    private final PreparedStatement getPduStatement;
    private final PreparedStatement deletePduStatement;
    private final PreparedStatement deleteAllPdusStatement;
    private final PreparedStatement getPdusStatement;
    private final PreparedStatement getPduViewsStatement;
    private final PreparedStatement getProxiesStatement;
    private final PreparedStatement getInterceptorsStatement;
    private final PreparedStatement getConnectionsStatement;
    private final PreparedStatement getTagsStatement;
    private final PreparedStatement getPduMetadataStatement;
    private final PreparedStatement getPduTagsStatement;
    private final Map<String, PreparedStatement> createEntityWithCodeAndNameStatement;
    private final Map<String, PreparedStatement> createEntityWithCodeStatement;
    private final Map<String, PreparedStatement> getEntityIdByCodeAndNameStatement;
    private final Map<String, PreparedStatement> getEntityIdByCodeStatement;
    private final PreparedStatement createPduStatement;
    private final PreparedStatement addPduTagStatement;
    private final PreparedStatement addPduMetadataStatement;
    private final PreparedStatement deleteUnusedTagsStatement;
    private final PreparedStatement deleteUnusedMetadataStatement;
    private final PreparedStatement deleteUnusedProxiesStatement;
    private final PreparedStatement deleteUnusedInterceptorsStatement;
    private final PreparedStatement deleteUnusedConnectionsStatement;

    private final HistoryDatabase database;

    /**
     * Constructs database history repository by creating SQLite database and preparing statements.
     * @param url Path to database file
     * @throws SQLException If anything fails during history database processing
     */
    public DatabaseHistoryRepository(String url) throws SQLException {
        this.database = new HistoryDatabase(url);

        var connection = database.getConnection();

        // Statement preparation
        getPduStatement = connection.prepareStatement(SQL_GET_PDU);
        deletePduStatement = connection.prepareStatement(SQL_DELETE_PDU);
        deleteAllPdusStatement = connection.prepareStatement(SQL_DELETE_ALL_PDUS);
        getPdusStatement = connection.prepareStatement(SQL_GET_PDUS);
        getPduViewsStatement = connection.prepareStatement(SQL_GET_PDU_VIEWS);
        getProxiesStatement = connection.prepareStatement(SQL_GET_PROXIES);
        getInterceptorsStatement = connection.prepareStatement(SQL_GET_INTERCEPTORS);
        getConnectionsStatement = connection.prepareStatement(SQL_GET_CONNECTIONS);
        getTagsStatement = connection.prepareStatement(SQL_GET_TAGS);
        getPduMetadataStatement = connection.prepareStatement(SQL_GET_PDU_METADATA);
        getPduTagsStatement = connection.prepareStatement(SQL_GET_PDU_TAGS);
        createPduStatement = connection.prepareStatement(SQL_CREATE_PDU, Statement.RETURN_GENERATED_KEYS);
        addPduTagStatement = connection.prepareStatement(SQL_ADD_PDU_TAG);
        addPduMetadataStatement = connection.prepareStatement(SQL_ADD_PDU_METADATA);
        deleteUnusedTagsStatement = connection.prepareStatement(SQL_DELETE_UNUSED_TAGS);
        deleteUnusedMetadataStatement = connection.prepareStatement(SQL_DELETE_UNUSED_METADATA);
        deleteUnusedProxiesStatement = connection.prepareStatement(SQL_DELETE_UNUSED_PROXIES);
        deleteUnusedInterceptorsStatement = connection.prepareStatement(SQL_DELETE_UNUSED_INTERCEPTORS);
        deleteUnusedConnectionsStatement = connection.prepareStatement(SQL_DELETE_UNUSED_CONNECTIONS);
        createEntityWithCodeAndNameStatement = Map.of(
                TABLE_PROXY,
                prepareStatementForTable(TABLE_PROXY, SQL_CREATE_ENTITY_WITH_CODE_AND_NAME),
                TABLE_CONNECTION,
                prepareStatementForTable(TABLE_CONNECTION, SQL_CREATE_ENTITY_WITH_CODE_AND_NAME),
                TABLE_INTERCEPTOR,
                prepareStatementForTable(TABLE_INTERCEPTOR, SQL_CREATE_ENTITY_WITH_CODE_AND_NAME)
        );
        createEntityWithCodeStatement = Map.of(
                TABLE_CHARSET,
                prepareStatementForTable(TABLE_CHARSET, SQL_CREATE_ENTITY_WITH_CODE),
                TABLE_TAG,
                prepareStatementForTable(TABLE_TAG, SQL_CREATE_ENTITY_WITH_CODE),
                TABLE_METADATA,
                prepareStatementForTable(TABLE_METADATA, SQL_CREATE_ENTITY_WITH_CODE)
        );
        getEntityIdByCodeAndNameStatement = Map.of(
                TABLE_PROXY,
                prepareStatementForTable(TABLE_PROXY, SQL_GET_ENTITY_ID_BY_CODE_AND_NAME),
                TABLE_CONNECTION,
                prepareStatementForTable(TABLE_CONNECTION, SQL_GET_ENTITY_ID_BY_CODE_AND_NAME),
                TABLE_INTERCEPTOR,
                prepareStatementForTable(TABLE_INTERCEPTOR, SQL_GET_ENTITY_ID_BY_CODE_AND_NAME)
        );
        getEntityIdByCodeStatement = Map.of(
                TABLE_CHARSET,
                prepareStatementForTable(TABLE_CHARSET, SQL_GET_ENTITY_ID_BY_CODE),
                TABLE_TAG,
                prepareStatementForTable(TABLE_TAG, SQL_GET_ENTITY_ID_BY_CODE),
                TABLE_METADATA,
                prepareStatementForTable(TABLE_METADATA, SQL_GET_ENTITY_ID_BY_CODE)
        );
    }

    @Override
    public void close() {
        database.close();
    }

    @Override
    public Optional<HistoricPdu> getPdu(long id) {
        try {
            getPduStatement.setLong(1, id);
            try (var result = getPduStatement.executeQuery()) {
                if (result.next()) {
                    return Optional.of(
                            mapResultToHistoricPdu(result)
                    );
                }
            }
        } catch (SQLException exception) {
            logError(exception);
        }
        return Optional.empty();
    }

    @Override
    public List<Long> deletePdus(Collection<Long> ids) {
        try {
            for (var id : ids) {
                deletePduStatement.setLong(1, id);
                deletePduStatement.addBatch();
            }
            var result = deletePduStatement.executeBatch();

            var deletedIds = new LinkedList<Long>();
            var index = 0;
            for (var id : ids) {
                if (result[index++] != 0) {
                    deletedIds.add(id);
                }
            }
            return Collections.unmodifiableList(deletedIds);
        } catch (SQLException exception) {
            logError(exception);
        }
        return List.of();
    }

    @Override
    public void deleteAllPdus() {
        try {
            deleteAllPdusStatement.executeUpdate();
        } catch (SQLException exception) {
            logError(exception);
        }
    }

    @Override
    public List<HistoricPdu> getPdus() {
        var list = new LinkedList<HistoricPdu>();
        try (var result = getPdusStatement.executeQuery()) {
            while (result.next()) {
                list.add(
                        mapResultToHistoricPdu(result)
                );
            }
        } catch (SQLException exception) {
            logError(exception);
        }
        return list;
    }

    @Override
    public List<HistoricPduView> getPduViews() {
        var list = new LinkedList<HistoricPduView>();
        try (var result = getPduViewsStatement.executeQuery()) {
            while (result.next()) {
                list.add(
                        mapResultToHistoricPduView(result)
                );
            }
        } catch (SQLException exception) {
            logError(exception);
        }
        return list;
    }

    @Override
    public List<HistoricPdu> getPdusByFilter(HistoryFilter filter) {
        if (filter.isEmpty()) {
            return getPdus();
        }

        var sql = generateFilterQuery(SQL_GET_PDUS, filter);
        var list = new LinkedList<HistoricPdu>();

        try (var stmt = database.getConnection().prepareStatement(sql)) {
            bindFilterParameters(stmt, filter);

            try (var result = stmt.executeQuery()) {
                while (result.next()) {
                    list.add(
                            mapResultToHistoricPdu(result)
                    );
                }
            }
        } catch (SQLException exception) {
            logError(exception);
        }
        return list;
    }

    @Override
    public List<HistoricPduView> getPduViewsByFilter(HistoryFilter filter) {
        if (filter.isEmpty()) {
            return getPduViews();
        }

        var sql = generateFilterQuery(SQL_GET_PDU_VIEWS, filter);
        var list = new LinkedList<HistoricPduView>();

        try (var stmt = database.getConnection().prepareStatement(sql)) {
            bindFilterParameters(stmt, filter);

            try (var result = stmt.executeQuery()) {
                while (result.next()) {
                    list.add(
                            mapResultToHistoricPduView(result)
                    );
                }
            }
        } catch (SQLException exception) {
            logError(exception);
        }
        return list;
    }

    @Override
    public Set<Long> getPduIdsByFilter(HistoryFilter filter) {
        if (filter.isEmpty()) {
            return Collections.emptySet();
        }

        var sql = generateFilterQuery(SQL_GET_PDU_IDS, filter);
        var list = new HashSet<Long>();

        try (var stmt = database.getConnection().prepareStatement(sql)) {
            bindFilterParameters(stmt, filter);

            try (var result = stmt.executeQuery()) {
                while (result.next()) {
                    list.add(result.getLong(1));
                }
            }
        } catch (SQLException exception) {
            logError(exception);
        }
        return list;
    }

    @Override
    public List<HistoricProxy> getProxies() {
        var list = new LinkedList<HistoricProxy>();
        try (var result = getProxiesStatement.executeQuery()) {
            while (result.next()) {
                list.add(
                        HistoricProxy.builder()
                            .id(result.getLong(1))
                            .code(result.getString(2))
                            .name(result.getString(3).intern())
                        .build()
                );
            }
        } catch (SQLException exception) {
            logError(exception);
        }
        return list;
    }

    @Override
    public List<HistoricInterceptor> getInterceptors() {
        var list = new LinkedList<HistoricInterceptor>();
        try (var result = getInterceptorsStatement.executeQuery()) {
            while (result.next()) {
                list.add(
                        HistoricInterceptor.builder()
                                .id(result.getLong(1))
                                .code(result.getString(2))
                                .name(result.getString(3).intern())
                                .build()
                );
            }
        } catch (SQLException exception) {
            logError(exception);
        }
        return list;
    }

    @Override
    public List<HistoricConnection> getConnections() {
        var list = new LinkedList<HistoricConnection>();
        try (var result = getConnectionsStatement.executeQuery()) {
            while (result.next()) {
                list.add(
                        HistoricConnection.builder()
                                .id(result.getLong(1))
                                .code(result.getString(2))
                                .name(result.getString(3).intern())
                                .build()
                );
            }
        } catch (SQLException exception) {
            logError(exception);
        }
        return list;
    }

    @Override
    public Set<String> getTags() {
        try {
            try (var result = getTagsStatement.executeQuery()) {
                var set = new HashSet<String>();
                while (result.next()) {
                    set.add(result.getString(1).intern());
                }
                return set;
            }
        } catch (SQLException exception) {
            logError(exception);
        }
        return Set.of();
    }

    @Override
    public Map<String, String> getPduMetadata(long id) {
        try {
            getPduMetadataStatement.setLong(1, id);

            try (var result = getPduMetadataStatement.executeQuery()) {
                var map = new HashMap<String, String>();
                while (result.next()) {
                    map.put(result.getString(1), result.getString(2));
                }
                return map;
            }
        } catch (SQLException exception) {
            logError(exception);
        }
        return Map.of();
    }

    @Override
    public Set<String> getPduTags(long id) {
        try {
            getPduTagsStatement.setLong(1, id);

            try (var result = getPduTagsStatement.executeQuery()) {
                Set<String> set = null;
                while (result.next()) {
                    if (set == null) {
                        set = new HashSet<>(4);
                    }
                    set.add(result.getString(1).intern());
                }
                if (set == null) {
                    return Collections.emptySet();
                }
                return set;
            }
        } catch (SQLException exception) {
            logError(exception);
        }
        return Set.of();
    }

    @Override
    public Optional<Long> getProxyIdByCodeAndName(String code, String name) {
        return getIdByCodeAndName(TABLE_PROXY, code, name);
    }

    @Override
    public Optional<Long> getConnectionIdByCodeAndName(String code, String name) {
        return getIdByCodeAndName(TABLE_CONNECTION, code, name);
    }

    @Override
    public Optional<Long> getInterceptorIdByCodeAndName(String code, String name) {
        return getIdByCodeAndName(TABLE_INTERCEPTOR, code, name);
    }

    @Override
    public Optional<Long> getCharsetId(Charset charset) {
        return getIdByCode(TABLE_CHARSET, charset.name());
    }

    @Override
    public Optional<Long> getTagIdByCode(String code) {
        return getIdByCode(TABLE_TAG, code);
    }

    @Override
    public Optional<Long> getMetadataIdByCode(String code) {
        return getIdByCode(TABLE_METADATA, code);
    }

    @Override
    public Collection<Long> getTagIdsByCodes(Collection<String> codes) {
        return codes.stream()
                .map(this::getTagIdByCode)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Long> createPdu(Long proxyId, Long connectionId, Long interceptorId, Long charsetId, PduDestination destination, int size, Instant time, byte[] data) {
        try {
            createPduStatement.setLong(1, proxyId);
            createPduStatement.setLong(2, connectionId);
            createPduStatement.setLong(3, interceptorId);
            createPduStatement.setLong(4, charsetId);
            createPduStatement.setLong(5, getDestinationId(destination));
            createPduStatement.setInt(6, size);
            createPduStatement.setLong(7, time.getEpochSecond());
            createPduStatement.setBytes(8, data);
            createPduStatement.executeUpdate();
            return Optional.of(createPduStatement.getGeneratedKeys().getLong(1));
        } catch (SQLException exception) {
            logError(exception);
        }
        return Optional.empty();
    }

    @Override
    public boolean addPduTags(Long pduId, Collection<Long> tagIds) {
        try {
            for (var tagId : tagIds) {
                addPduTagStatement.setLong(1, pduId);
                addPduTagStatement.setLong(2, tagId);
                addPduTagStatement.addBatch();
            }
            addPduTagStatement.executeBatch();
            return true;
        } catch (SQLException exception) {
            logError(exception);
        }
        return false;
    }

    @Override
    public boolean addPduMetadata(Long pduId, Map<String, String> metadata) {
        try {
            for (var item : metadata.entrySet()) {
                var metadataId = getMetadataIdByCode(item.getKey());
                if (metadataId.isEmpty()) {
                    return false;
                }
                addPduMetadataStatement.setLong(1, pduId);
                addPduMetadataStatement.setLong(2, metadataId.get());
                addPduMetadataStatement.setString(3, item.getValue());
                addPduMetadataStatement.addBatch();
            }
            addPduMetadataStatement.executeBatch();
            return true;
        } catch (SQLException exception) {
            logError(exception);
        }
        return false;
    }

    @Override
    public void deleteUnusedRecords() {
        try {
            deleteUnusedTagsStatement.executeUpdate();
            deleteUnusedMetadataStatement.executeUpdate();
            deleteUnusedProxiesStatement.executeUpdate();
            deleteUnusedInterceptorsStatement.executeUpdate();
            deleteUnusedConnectionsStatement.executeUpdate();
        } catch (SQLException exception) {
            logError(exception);
        }
    }

    /**
     * Gets entity identifier by specified code and name from given table. (Creates it if it does not exist.)
     * @param table Target table
     * @param code Code of the entity to be found
     * @param name Name of the entity to be found
     * @return Identifier of the obtained entity
     */
    protected Optional<Long> getIdByCodeAndName(String table, String code, String name) {
        try {
            var stmt = getEntityIdByCodeAndNameStatement.get(table);
            stmt.setString(1, code);
            stmt.setString(2, name);
            try (var result = stmt.executeQuery()) {
                if (result.next()) {
                    return Optional.of(result.getLong(1));
                }
                return createEntityAndGetId(table, code, name);
            }
        } catch (SQLException exception) {
            logError(exception);
        }
        return Optional.empty();
    }

    /**
     * Creates entity with specified code and name in given table.
     * @param table Target table
     * @param code Code of the entity to be created
     * @param name Name of the entity to be created
     * @return Identifier of the created entity
     */
    protected Optional<Long> createEntityAndGetId(String table, String code, String name) {
        try {
            var stmt = createEntityWithCodeAndNameStatement.get(table);
            stmt.setString(1, code);
            stmt.setString(2, name);
            stmt.executeUpdate();
            return Optional.of(stmt.getGeneratedKeys().getLong(1));
        } catch (SQLException exception) {
            logError(exception);
        }
        return Optional.empty();
    }

    /**
     * Gets entity identifier by specified code from given table. (Creates it if it does not exist.)
     * @param table Target table
     * @param code Code of the entity to be found
     * @return Identifier of the obtained entity
     */
    protected Optional<Long> getIdByCode(String table, String code) {
        try {
            var stmt = getEntityIdByCodeStatement.get(table);
            stmt.setString(1, code);
            try (var result = stmt.executeQuery()) {
                if (result.next()) {
                    return Optional.of(result.getLong(1));
                }
                return createEntityAndGetId(table, code);
            }
        } catch (SQLException exception) {
            logError(exception);
        }
        return Optional.empty();
    }

    /**
     * Creates entity with specified code in given table.
     * @param table Target table
     * @param code Code of the entity to be created
     * @return Identifier of the created entity
     */
    protected Optional<Long> createEntityAndGetId(String table, String code) {
        try {
            var stmt = createEntityWithCodeStatement.get(table);
            stmt.setString(1, code);
            stmt.executeUpdate();
            return Optional.of(stmt.getGeneratedKeys().getLong(1));
        } catch (SQLException exception) {
            logError(exception);
        }
        return Optional.empty();
    }

    /**
     * Prepares statement for given table (replaces %s with table name).
     * @param table Table to be used instead of %s
     * @param sql SQL query to format
     * @return Prepared SQL statement
     */
    private PreparedStatement prepareStatementForTable(String table, String sql) throws SQLException {
        return database.getConnection().prepareStatement(
                String.format(sql, table),
                Statement.RETURN_GENERATED_KEYS);
    }

    /**
     * Maps SQL result set to historic PDU.
     * @param result Result set to be mapped
     * @return Returns historic PDU
     */
    protected HistoricPdu mapResultToHistoricPdu(ResultSet result) throws SQLException {
        var id = result.getLong(1);
        return HistoricPdu.builder()
                .id(id)
                .destination(getDestination(result.getInt(2)))
                .size(result.getInt(3))
                .time(Instant.ofEpochSecond(result.getInt(4)))
                .data(result.getBytes(5))
                .proxy(
                        HistoricProxy.builder()
                                .id(result.getLong(6))
                                .code(result.getString(7))
                                .name(result.getString(8).intern())
                                .build())
                .connection(
                        HistoricConnection.builder()
                                .id(result.getLong(9))
                                .code(result.getString(10))
                                .name(result.getString(11).intern())
                                .build())
                .interceptor(
                        HistoricInterceptor.builder()
                                .id(result.getLong(12))
                                .code(result.getString(13))
                                .name(result.getString(14).intern())
                                .build())
                .charset(Charset.forName(result.getString(15)))
                .metadata(getPduMetadata(id))
                .tags(getPduTags(id))
                .build();
    }

    /**
     * Maps SQL result set to historic PDU view.
     * @param result Result set to be mapped
     * @return Returns historic PDU view
     */
    protected HistoricPduView mapResultToHistoricPduView(ResultSet result) throws SQLException {
        var id = result.getLong(1);
        return HistoricPduView.builder()
                .id(id)
                .destination(getDestination(result.getInt(2)))
                .size(result.getInt(3))
                .time(Instant.ofEpochSecond(result.getInt(4)))
                .proxyId(result.getLong(5))
                .proxyName(result.getString(6).intern())
                .connectionId(result.getLong(7))
                .connectionName(result.getString(8).intern())
                .interceptorId(result.getLong(9))
                .interceptorName(result.getString(10).intern())
                .tags(getPduTags(id))
                .build();
    }
}
