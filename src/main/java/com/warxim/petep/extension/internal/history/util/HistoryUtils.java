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

import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.extension.internal.history.model.HistoricPdu;
import com.warxim.petep.extension.internal.history.model.HistoricPduView;

/**
 * Utils for history extension
 */
@PetepAPI
public final class HistoryUtils {
    /**
     * Converts historic PDU to historic PDU view
     * @param pdu Historic PDU to be converted
     * @return Historic PDU view for given historic PDU
     */
    public static HistoricPduView historicPduToView(HistoricPdu pdu) {
        return HistoricPduView.builder()
                .id(pdu.getId())
                .proxyId(pdu.getProxy().getId())
                .proxyName(pdu.getProxy().getName())
                .connectionId(pdu.getConnection().getId())
                .connectionName(pdu.getConnection().getName())
                .interceptorId(pdu.getInterceptor().getId())
                .interceptorName(pdu.getInterceptor().getName())
                .tags(pdu.getTags())
                .destination(pdu.getDestination())
                .size(pdu.getSize())
                .time(pdu.getTime())
                .build();
    }

    private HistoryUtils() {}
}
