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
package com.warxim.petep.extension.internal.history.service;

import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.extension.internal.history.model.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * History service is used for querying history.
 */
@PetepAPI
public interface HistoryService {
    /**
     * Saves PDU to history repository
     * @param pdu PDU to be saved
     * @return Completable future for identifier of created PDU
     */
    CompletableFuture<Optional<Long>> savePdu(HistoricPdu pdu);

    /**
     * Gets PDU from history repository
     * @param id PDU identifier
     * @return Completable future for PDU
     */
    CompletableFuture<Optional<HistoricPdu>> getPdu(long id);

    /**
     * Deletes PDU from history repository
     * @param id PDU identifier
     * @return Completable future for boolean that signalizes if the deletion has been completed successfully
     */
    CompletableFuture<Boolean> deletePdu(long id);

    /**
     * Deletes PDUs from history repository
     * @param ids PDU identifiers
     * @return Completable future for list of identifiers of PDUs, which have been successfully deleted
     */
    CompletableFuture<List<Long>> deletePdus(Collection<Long> ids);

    /**
     * Clears the history.
     * @return Completable future signalizing that the history has been cleaned
     */
    CompletableFuture<Void> clearHistory();

    /**
     * Gets all PDUs from history repository
     * @return Completable future for list of obtained PDUs
     */
    CompletableFuture<List<HistoricPdu>> getPdus();

    /**
     * Gets all PDU views from history repository
     * @return Completable future for list of obtained PDU views
     */
    CompletableFuture<List<HistoricPduView>> getPduViews();

    /**
     * Gets PDUs that match given filter from history repository
     * @param filter Filter for filtering PDUs
     * @return Completable future for list of obtained PDUs
     */
    CompletableFuture<List<HistoricPdu>> getPdusByFilter(HistoryFilter filter);

    /**
     * Gets PDU views that match given filter from history repository
     * @param filter Filter for filtering PDU views
     * @return Completable future for list of obtained PDU views
     */
    CompletableFuture<List<HistoricPduView>> getPduViewsByFilter(HistoryFilter filter);

    /**
     * Gets PDU identifiers that match given filter from history repository
     * @param filter Filter for filtering PDU identifiers
     * @return Completable future for list of obtained PDU identifiers
     */
    CompletableFuture<Set<Long>> getPduIdsByFilter(HistoryFilter filter);

    /**
     * Gets all proxies from history repository
     * @return Completable future for list of obtained proxies
     */
    CompletableFuture<List<HistoricProxy>> getProxies();

    /**
     * Gets all interceptors from history repository
     * @return Completable future for list of obtained proxies
     */
    CompletableFuture<List<HistoricInterceptor>> getInterceptors();

    /**
     * Gets all connections from history repository
     * @return Completable future for list of obtained proxies
     */
    CompletableFuture<List<HistoricConnection>> getConnections();

    /**
     * Gets all tags from history repository
     * @return Completable future for list of obtained proxies
     */
    CompletableFuture<Set<String>> getTags();
}
