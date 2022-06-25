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
import com.warxim.petep.extension.internal.history.model.*;
import com.warxim.petep.extension.internal.history.repository.HistoryRepository;
import com.warxim.petep.extension.internal.history.util.HistoryUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default history service
 * <p>Uses single thread to serve data.</p>
 */
public class DefaultHistoryService implements HistoryService {
    /**
     * How many seconds to wait before terminating executor.
     */
    private static final int TERMINATION_TIMEOUT_SECONDS = 60;

    protected ExecutorService executor;
    protected HistoryRepository repository;
    protected HistoryListener listener;

    /**
     * Constructs default history service.
     * @param repository Underlying repository to use in the service
     * @param listener History listener
     */
    public DefaultHistoryService(HistoryRepository repository, HistoryListener listener) {
        this.repository = repository;
        this.listener = listener;
        this.executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public CompletableFuture<Optional<Long>> savePdu(HistoricPdu pdu) {
        return CompletableFuture.supplyAsync(() -> processSavePdu(pdu), executor);
    }

    @Override
    public CompletableFuture<Optional<HistoricPdu>> getPdu(long id) {
        return CompletableFuture.supplyAsync(() -> repository.getPdu(id), executor);
    }

    @Override
    public CompletableFuture<Boolean> deletePdu(long id) {
        return CompletableFuture.supplyAsync(() -> processDeletePdus(List.of(id)).size() == 1, executor);
    }

    @Override
    public CompletableFuture<List<Long>> deletePdus(Collection<Long> ids) {
        return CompletableFuture.supplyAsync(() -> processDeletePdus(ids), executor);
    }

    @Override
    public CompletableFuture<Void> clearHistory() {
        return CompletableFuture.runAsync(this::processClearHistory, executor);
    }

    @Override
    public CompletableFuture<List<HistoricPdu>> getPdus() {
        return CompletableFuture.supplyAsync(() -> repository.getPdus(), executor);
    }

    @Override
    public CompletableFuture<List<HistoricPduView>> getPduViews() {
        return CompletableFuture.supplyAsync(() -> repository.getPduViews(), executor);
    }

    @Override
    public CompletableFuture<List<HistoricPdu>> getPdusByFilter(HistoryFilter filter) {
        return CompletableFuture.supplyAsync(() -> repository.getPdusByFilter(filter), executor);
    }

    @Override
    public CompletableFuture<List<HistoricPduView>> getPduViewsByFilter(HistoryFilter filter) {
        return CompletableFuture.supplyAsync(() -> repository.getPduViewsByFilter(filter), executor);
    }

    @Override
    public CompletableFuture<Set<Long>> getPduIdsByFilter(HistoryFilter filter) {
        return CompletableFuture.supplyAsync(() -> repository.getPduIdsByFilter(filter), executor);
    }

    @Override
    public CompletableFuture<List<HistoricProxy>> getProxies() {
        return CompletableFuture.supplyAsync(() -> repository.getProxies(), executor);
    }

    @Override
    public CompletableFuture<List<HistoricInterceptor>> getInterceptors() {
        return CompletableFuture.supplyAsync(() -> repository.getInterceptors(), executor);
    }

    @Override
    public CompletableFuture<List<HistoricConnection>> getConnections() {
        return CompletableFuture.supplyAsync(() -> repository.getConnections(), executor);
    }

    @Override
    public CompletableFuture<Set<String>> getTags() {
        return CompletableFuture.supplyAsync(() -> repository.getTags(), executor);
    }

    /**
     * Processes cleaning of the history.
     */
    protected void processClearHistory() {
        repository.deleteAllPdus();
        repository.deleteUnusedRecords();
        listener.onHistoryClear();
    }

    /**
     * Processes deletion of PDUs with given identifiers.
     * @param ids PDU identifiers
     * @return List of identifiers of deleted PDUs
     */
    protected List<Long> processDeletePdus(Collection<Long> ids) {
        var deletedIds = repository.deletePdus(ids);
        if (!deletedIds.isEmpty()) {
            listener.onHistoricPduDelete(deletedIds);
            repository.deleteUnusedRecords();
        }
        return deletedIds;
    }

    /**
     * Processes saving of PDU
     * @param pdu Historic PDU
     * @return Identifier of saved PDU if it has been saved successfully
     */
    protected Optional<Long> processSavePdu(HistoricPdu pdu) {
        var proxyId = repository.getProxyIdByCodeAndName(pdu.getProxy().getCode(), pdu.getProxy().getName());
        if (proxyId.isEmpty()) {
            return Optional.empty();
        }
        pdu.getProxy().setId(proxyId.get());

        var connectionId = repository.getConnectionIdByCodeAndName(pdu.getConnection().getCode(), pdu.getConnection().getName());
        if (connectionId.isEmpty()) {
            return Optional.empty();
        }
        pdu.getConnection().setId(connectionId.get());

        var interceptorId = repository.getInterceptorIdByCodeAndName(pdu.getInterceptor().getCode(), pdu.getInterceptor().getName());
        if (interceptorId.isEmpty()) {
            return Optional.empty();
        }
        pdu.getInterceptor().setId(interceptorId.get());

        var charsetId = repository.getCharsetId(pdu.getCharset());
        if (charsetId.isEmpty()) {
            return Optional.empty();
        }

        var optionalPduId = repository.createPdu(
                proxyId.get(),
                connectionId.get(),
                interceptorId.get(),
                charsetId.get(),
                pdu.getDestination(),
                pdu.getSize(),
                pdu.getTime(),
                pdu.getData());

        if (optionalPduId.isEmpty()) {
            return Optional.empty();
        }

        var pduId = optionalPduId.get();
        var tagIds = repository.getTagIdsByCodes(pdu.getTags());
        repository.addPduTags(pduId, tagIds);
        if (pdu.getMetadata() != null && !pdu.getMetadata().isEmpty()) {
            repository.addPduMetadata(pduId, pdu.getMetadata());
        }

        pdu.setId(pduId);

        listener.onHistoricPduCreate(pdu, HistoryUtils.historicPduToView(pdu));

        return optionalPduId;
    }

    /**
     * Stops the history service.
     */
    public void stop() {
        try {
            if (shutdownExecutor()) {
                Logger.getGlobal().severe("HistoryService stopped!");
                return;
            }
            Logger.getGlobal().severe("HistoryService executor was not stopped correctly!");
        } finally {
            try {
                repository.close();
            } catch (Exception e) {
                Logger.getGlobal().log(Level.SEVERE, "Could not close history service!", e);
            }
        }
    }

    /**
     * Shuts down the executor.
     */
    private boolean shutdownExecutor() {
        executor.shutdown();
        try {
            if (executor.awaitTermination(TERMINATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                return true;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        executor.shutdownNow();
        return false;
    }
}
