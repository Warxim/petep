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
package com.warxim.petep.extension.internal.history.util;

import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.history.model.HistoryFilter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utils for working with history database.
 */
public class HistoryDatabaseUtils {
    private static final String EXCEPTION_LOG_MESSAGE = "SQL exception occurred!";

    private HistoryDatabaseUtils() {}

    /**
     * Generates filter query
     * @param sql SQL query before WHERE clause
     * @param filter Filter to used for generation the query
     * @return SQL query with filter conditions
     */
    public static String generateFilterQuery(String sql, HistoryFilter filter) {
        var condition = new StringJoiner(" AND ");

        if (filter.getDestination() != null) {
            condition.add("destination = ?");
        }

        if (filter.getProxyId() != null) {
            condition.add("proxy.id = ?");
        }

        if (filter.getInterceptorId() != null) {
            condition.add("interceptor.id = ?");
        }

        if (filter.getConnectionId() != null) {
            condition.add("connection.id = ?");
        }

        if (filter.getFromSize() != null) {
            condition.add("pdu.size >= ?");
        }

        if (filter.getToSize() != null) {
            condition.add("pdu.size <= ?");
        }

        if (filter.getTags() != null && !filter.getTags().isEmpty()) {
            for (int i = 0; i < filter.getTags().size(); ++i) {
                condition.add("EXISTS (SELECT pht.id_pdu FROM pdu_has_tag pht LEFT JOIN tag ON tag.id = pht.id_tag WHERE pht.id_pdu = pdu.id AND tag.code = ?)");
            }
        }

        if (filter.getData() != null) {
            condition.add(generateDataCondition(filter));
        }

        var sqlBuilder = new StringBuilder(sql);
        sqlBuilder.append(" WHERE ");
        sqlBuilder.append(condition);
        return sqlBuilder.toString();
    }

    /**
     * Binds filter parameters to prepared statement
     * @param stmt Statement for binding parameters
     * @param filter Filter to bind
     * @throws SQLException If the bind fails
     */
    public static void bindFilterParameters(PreparedStatement stmt, HistoryFilter filter) throws SQLException {
        var paramId = 0;

        if (filter.getDestination() != null) {
            stmt.setInt(++paramId, getDestinationId(filter.getDestination()));
        }

        if (filter.getProxyId() != null) {
            stmt.setLong(++paramId, filter.getProxyId());
        }

        if (filter.getInterceptorId() != null) {
            stmt.setLong(++paramId, filter.getInterceptorId());
        }

        if (filter.getConnectionId() != null) {
            stmt.setLong(++paramId, filter.getConnectionId());
        }

        if (filter.getFromSize() != null) {
            stmt.setInt(++paramId, filter.getFromSize());
        }

        if (filter.getToSize() != null) {
            stmt.setInt(++paramId, filter.getToSize());
        }

        if (filter.getTags() != null && !filter.getTags().isEmpty()) {
            for (var tag : filter.getTags()) {
                stmt.setString(++paramId, tag);
            }
        }

        if (filter.getData() != null) {
            stmt.setBytes(++paramId, filter.getData());
        }
    }

    /**
     * Coverts PDU destination enum to integer ID.
     * @param destination Destination to be converted
     * @return Identifier of destination
     */
    public static int getDestinationId(PduDestination destination) {
        if (destination == PduDestination.SERVER) {
            return 0;
        }
        return 1;
    }

    /**
     * Coverts integer ID to PDU destination enum.
     * @param id Destination identifier
     * @return PDU Destination enum
     */
    public static PduDestination getDestination(int id) {
        if (id == 0) {
            return PduDestination.SERVER;
        }
        return PduDestination.CLIENT;
    }

    /**
     * Logs SQL exception to global logger.
     * @param exception Exception to be logged
     */
    public static void logError(SQLException exception) {
        Logger.getGlobal().log(Level.SEVERE, EXCEPTION_LOG_MESSAGE, exception);
    }

    /**
     * Generates data condition for given filter.
     */
    private static String generateDataCondition(HistoryFilter filter) {
        String dataCondition;
        if (filter.getDataFilterType() == HistoryFilter.DataFilterType.STARTS_WITH) {
            var substrLength = filter.getData().length;
            dataCondition = "substr(data, 1, " + substrLength + ") = ?";
        } else if (filter.getDataFilterType() == HistoryFilter.DataFilterType.ENDS_WITH) {
            var substrLength = filter.getData().length;
            dataCondition = "substr(data, -" + substrLength + ") = ?";
        } else {
            dataCondition = "INSTR(data, ?) > 0";
        }

        // Add the data condition
        if (filter.isDataFilterNegative()) {
            return "NOT(" + dataCondition + ")";
        } else {
            return dataCondition;
        }
    }

}
