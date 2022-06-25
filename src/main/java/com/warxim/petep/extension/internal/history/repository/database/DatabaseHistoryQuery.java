package com.warxim.petep.extension.internal.history.repository.database;

/**
 * SQL queries to history database.
 */
public final class DatabaseHistoryQuery {
    public static final String SQL_PDU_COLUMNS =
            "    pdu.id, pdu.destination, pdu.size, pdu.time, pdu.data," +
                    "    proxy.id, proxy.code, proxy.name," +
                    "    connection.id, connection.code, connection.name," +
                    "    interceptor.id, interceptor.code, interceptor.name," +
                    "    charset.code";

    public static final String SQL_PDU_VIEW_COLUMNS =
            "    pdu.id, pdu.destination, pdu.size, pdu.time," +
                    "    proxy.id, proxy.name," +
                    "    connection.id, connection.name," +
                    "    interceptor.id, interceptor.name";

    public static final String SQL_GET_PDUS =
            "SELECT " + SQL_PDU_COLUMNS +
                    " FROM pdu" +
                    " LEFT JOIN proxy ON proxy.id = pdu.id_proxy" +
                    " LEFT JOIN connection ON connection.id = pdu.id_connection" +
                    " LEFT JOIN interceptor ON interceptor.id = pdu.id_interceptor" +
                    " LEFT JOIN charset ON charset.id = pdu.id_charset";

    public static final String SQL_GET_PDU_VIEWS =
            "SELECT " + SQL_PDU_VIEW_COLUMNS +
                    " FROM pdu" +
                    " LEFT JOIN proxy ON proxy.id = pdu.id_proxy" +
                    " LEFT JOIN connection ON connection.id = pdu.id_connection" +
                    " LEFT JOIN interceptor ON interceptor.id = pdu.id_interceptor";

    public static final String SQL_GET_PDU_IDS =
            "SELECT pdu.id" +
                    " FROM pdu" +
                    " LEFT JOIN proxy ON proxy.id = pdu.id_proxy" +
                    " LEFT JOIN connection ON connection.id = pdu.id_connection" +
                    " LEFT JOIN interceptor ON interceptor.id = pdu.id_interceptor";

    public static final String SQL_GET_PROXIES =
            "SELECT id, code, name FROM proxy";

    public static final String SQL_GET_INTERCEPTORS =
            "SELECT id, code, name FROM interceptor";

    public static final String SQL_GET_CONNECTIONS =
            "SELECT id, code, name FROM connection";

    public static final String SQL_GET_TAGS =
            "SELECT tag.code FROM tag";

    public static final String SQL_GET_PDU =
            SQL_GET_PDUS +
                    " WHERE pdu.id = ?";

    public static final String SQL_DELETE_PDU = "DELETE FROM pdu WHERE id = ?";

    public static final String SQL_DELETE_ALL_PDUS = "DELETE FROM pdu";

    public static final String SQL_GET_PDU_METADATA =
            "SELECT" +
                    "    metadata.code," +
                    "    pdu_has_metadata.value" +
                    " FROM pdu_has_metadata" +
                    " LEFT JOIN metadata ON metadata.id = pdu_has_metadata.id_metadata" +
                    " WHERE pdu_has_metadata.id_pdu = ?";

    public static final String SQL_GET_PDU_TAGS =
            "SELECT" +
                    "    tag.code" +
                    " FROM pdu_has_tag" +
                    " LEFT JOIN tag ON tag.id = pdu_has_tag.id_tag" +
                    " WHERE pdu_has_tag.id_pdu = ?";

    public static final String SQL_CREATE_ENTITY_WITH_CODE_AND_NAME = "INSERT INTO %s(code, name) VALUES(?, ?)";

    public static final String SQL_CREATE_ENTITY_WITH_CODE = "INSERT INTO %s(code) VALUES(?)";

    public static final String SQL_GET_ENTITY_ID_BY_CODE_AND_NAME = "SELECT id FROM %s WHERE code = ? AND name = ?";

    public static final String SQL_GET_ENTITY_ID_BY_CODE = "SELECT id FROM %s WHERE code = ?";

    public static final String SQL_CREATE_PDU =
            "INSERT INTO pdu(id_proxy, id_connection, id_interceptor, id_charset, destination, size, time, data) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_ADD_PDU_TAG = "INSERT INTO pdu_has_tag(id_pdu, id_tag) VALUES(?, ?)";

    public static final String SQL_ADD_PDU_METADATA = "INSERT INTO pdu_has_metadata(id_pdu, id_metadata, value) VALUES(?, ?, ?)";

    public static final String SQL_DELETE_UNUSED_TAGS =
            "DELETE FROM tag"
                    + " WHERE id IN ("
                    + "SELECT id FROM tag"
                    + " LEFT JOIN pdu_has_tag ON tag.id = pdu_has_tag.id_tag"
                    + " WHERE pdu_has_tag.id_tag IS NULL"
                    + ")";

    public static final String SQL_DELETE_UNUSED_METADATA =
            "DELETE FROM metadata"
                    + " WHERE id IN ("
                    + "SELECT id FROM metadata"
                    + " LEFT JOIN pdu_has_metadata ON metadata.id = pdu_has_metadata.id_metadata"
                    + " WHERE pdu_has_metadata.id_metadata IS NULL"
                    + ")";

    public static final String SQL_DELETE_UNUSED_PROXIES =
            "DELETE FROM proxy"
                    + " WHERE id IN ("
                    + "SELECT proxy.id FROM proxy"
                    + " LEFT JOIN pdu ON proxy.id = pdu.id_proxy"
                    + " WHERE pdu.id_proxy IS NULL"
                    + ")";

    public static final String SQL_DELETE_UNUSED_INTERCEPTORS =
            "DELETE FROM interceptor"
                    + " WHERE id IN ("
                    + "SELECT interceptor.id FROM interceptor"
                    + " LEFT JOIN pdu ON interceptor.id = pdu.id_interceptor"
                    + " WHERE pdu.id_interceptor IS NULL"
                    + ")";

    public static final String SQL_DELETE_UNUSED_CONNECTIONS =
            "DELETE FROM connection"
                    + " WHERE id IN ("
                    + "SELECT connection.id FROM connection"
                    + " LEFT JOIN pdu ON connection.id = pdu.id_connection"
                    + " WHERE pdu.id_connection IS NULL"
                    + ")";
    
    public DatabaseHistoryQuery() {
    }
    
}
