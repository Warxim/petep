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

import com.warxim.petep.extension.internal.history.listener.HistoryListener;
import com.warxim.petep.extension.internal.history.model.HistoricPdu;
import com.warxim.petep.extension.internal.history.model.HistoricPduView;
import com.warxim.petep.extension.internal.history.model.HistoryFilter;
import com.warxim.petep.extension.internal.history.repository.HistoryRepository;
import com.warxim.petep.extension.internal.history.util.HistoryUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Cached history service
 * <p>Uses cached list of HistoricPduViews.</p>
 */
public class CachedHistoryService extends DefaultHistoryService {
    private List<HistoricPduView> pduViewCache;

    /**
     * Constructs cached history service, which uses default history service and caches pdu views.
     * @param repository Underlying repository to use in the service
     * @param listener History listener
     * @throws ExecutionException if it was not possible to obtain PDU views
     * @throws InterruptedException if the current thread was interrupted during wait for PDU views
     */
    public CachedHistoryService(HistoryRepository repository, HistoryListener listener)
            throws ExecutionException, InterruptedException {
        super(repository, listener);
        pduViewCache = new LinkedList<>(super.getPduViews().get());
    }

    @Override
    protected Optional<Long> processSavePdu(HistoricPdu pdu) {
        var pduId = super.processSavePdu(pdu);
        if (pduId.isPresent()) {
            pduViewCache.add(HistoryUtils.historicPduToView(pdu));
        }
        return pduId;
    }

    @Override
    public CompletableFuture<List<HistoricPduView>> getPduViews() {
        return CompletableFuture.supplyAsync(
                () -> new LinkedList<>(pduViewCache),
                executor);
    }

    @Override
    public CompletableFuture<List<HistoricPduView>> getPduViewsByFilter(HistoryFilter filter) {
        if (filter.getData() != null) {
            // If there is filter for data, we have to use SQL based filtering, because pduView does not contain data
            return CompletableFuture.supplyAsync(
                    () -> {
                        var ids = repository.getPduIdsByFilter(filter);
                        return pduViewCache.stream()
                                .filter(pduView -> ids.contains(pduView.getId()))
                                .collect(Collectors.toCollection(LinkedList::new));
                    },
                    executor);
        }
        // There is no filter for data, we can simply filter cached pduViews
        return CompletableFuture.supplyAsync(
                () -> pduViewCache.stream()
                        .filter(filter::matches)
                        .collect(Collectors.toCollection(LinkedList::new)), executor);
    }

    @Override
    protected List<Long> processDeletePdus(Collection<Long> ids) {
        var deletedIds = super.processDeletePdus(ids);
        if (!deletedIds.isEmpty()) {
            var tempIds = new HashSet<>(deletedIds);
            pduViewCache.removeIf(pdu -> tempIds.remove(pdu.getId()));
        }
        return deletedIds;
    }

    @Override
    protected void processClearHistory() {
        super.processClearHistory();
        pduViewCache.clear();
    }
}
