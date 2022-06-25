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
package com.warxim.petep.extension.internal.history.model;

import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.util.BytesUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.Charset;
import java.util.Set;

/**
 * History filter for filtering PDUs in history extension.
 * <p><b>Note:</b> PduView cannot be filtered using data filter.</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@PetepAPI
public class HistoryFilter {
    /**
     * Filter by PDU destination
     */
    private PduDestination destination;
    /**
     * Filter by proxy using the database identifier
     */
    private Long proxyId;
    /**
     * Filter by interceptor using the database identifier
     */
    private Long interceptorId;
    /**
     * Filter by connection using the database identifier
     */
    private Long connectionId;
    /**
     * Filter by minimum PDU size
     */
    private Integer fromSize;
    /**
     * Filter by maximum PDU size
     */
    private Integer toSize;
    /**
     * Filter by tags
     */
    private Set<String> tags;
    /**
     * Filter by data
     */
    private byte[] data;
    /**
     * Charset of data provided in {@link HistoryFilter#data}
     */
    private Charset dataCharset;
    /**
     * Type of data filter {@link HistoryFilter#data}
     */
    private DataFilterType dataFilterType;
    /**
     * Should data filter be used as negative search {@link HistoryFilter#data}
     */
    private boolean dataFilterNegative;

    /**
     * Checks whether the provided pduView complies the filter.
     * <p><b>Warning: Since {@link HistoricPduView} does not contain data buffer, it ignores data filter field.</b></p>
     * @param pduView PDU view to check
     * @return {@code true} if pduView complies with the filter
     */
    public boolean matches(HistoricPduView pduView) {
        if (destination != null && pduView.getDestination() != destination) {
            return false;
        }

        if (proxyId != null && !proxyId.equals(pduView.getProxyId())) {
            return false;
        }

        if (interceptorId != null && !interceptorId.equals(pduView.getInterceptorId())) {
            return false;
        }

        if (connectionId != null && !connectionId.equals(pduView.getConnectionId())) {
            return false;
        }

        if (fromSize != null && fromSize > pduView.getSize()) {
            return false;
        }

        if (toSize != null && toSize < pduView.getSize()) {
            return false;
        }

        return tags == null || pduView.getTags().containsAll(tags);
    }

    /**
     * Checks whether the provided pdu complies the filter.
     * @param pdu PDU to check against filter
     * @return {@code true} if pduView complies with the filter
     */
    public boolean matches(HistoricPdu pdu) {
        if (destination != null && pdu.getDestination() != destination) {
            return false;
        }

        if (proxyId != null && !proxyId.equals(pdu.getProxy().getId())) {
            return false;
        }

        if (interceptorId != null && !interceptorId.equals(pdu.getInterceptor().getId())) {
            return false;
        }

        if (connectionId != null && !connectionId.equals(pdu.getConnection().getId())) {
            return false;
        }

        if (fromSize != null && fromSize > pdu.getSize()) {
            return false;
        }

        if (toSize != null && toSize < pdu.getSize()) {
            return false;
        }

        if (tags != null && !pdu.getTags().containsAll(tags)) {
            return false;
        }

        if (data != null) {
            boolean result;
            if (dataFilterType == DataFilterType.STARTS_WITH) {
                result = BytesUtils.startsWith(pdu.getData(), pdu.getSize(), data);
            } else if (dataFilterType == DataFilterType.ENDS_WITH) {
                result = BytesUtils.endsWith(pdu.getData(), pdu.getSize(), data);
            } else {
                // CONTAINS is default
                result = BytesUtils.contains(pdu.getData(), pdu.getSize(), data);
            }
            return dataFilterNegative != result;
        }

        return true;
    }

    /**
     * Checks whether the filter is empty.
     * @return {@code true} if the filter is empty (no filtering is set)
     */
    public boolean isEmpty() {
        if (destination != null) {
            return false;
        }

        if (proxyId != null) {
            return false;
        }

        if (interceptorId != null) {
            return false;
        }

        if (connectionId != null) {
            return false;
        }

        if (fromSize != null) {
            return false;
        }

        if (toSize != null) {
            return false;
        }

        if (tags != null && !tags.isEmpty()) {
            return false;
        }

        return data == null;
    }

    /**
     * Generates filter for getting all PDUs.
     * @return History filter
     */
    public static HistoryFilter all() {
        return new HistoryFilter();
    }

    /**
     * Type of data filter
     */
    public enum DataFilterType {
        /**
         * Checks whether the historic PDU contains specified data.
         */
        CONTAINS,
        /**
         * Checks whether the historic PDU starts with specified data.
         */
        STARTS_WITH,
        /**
         * Checks whether the historic PDU ends with specified data.
         */
        ENDS_WITH
    }
}
