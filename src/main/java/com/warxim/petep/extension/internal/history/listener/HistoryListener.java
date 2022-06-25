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
package com.warxim.petep.extension.internal.history.listener;

import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.extension.internal.history.model.HistoricPdu;
import com.warxim.petep.extension.internal.history.model.HistoricPduView;

import java.util.Collection;

/**
 * History listener for listening to events of History.
 * <p>There are create, delete and clear events produced.</p>
 * <p>Listeners can be registered through {@link com.warxim.petep.extension.internal.history.service.HistoryService}.</p>
 * <p>Since these listeners are stored as weak references, strong reference should be kept somewhere in the code.</p>
 */
@PetepAPI
public interface HistoryListener {
    /**
     * Handles creation of historic PDU.
     * @param pdu Created PDU, which has been persisted to database
     * @param pduView View of the created PDU
     */
    default void onHistoricPduCreate(HistoricPdu pdu, HistoricPduView pduView) {}

    /**
     * Handles deletion of historic PDUs.
     * @param ids Collection of identifiers of PDUs, which have been deleted
     */
    default void onHistoricPduDelete(Collection<Long> ids) {}

    /**
     * Handles deletion of all historic items (PDUs, tags, ...).
     */
    default void onHistoryClear() {}
}
