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
package com.warxim.petep.extension.internal.history.interceptor;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.internal.history.HistoryApi;
import com.warxim.petep.extension.internal.history.model.HistoricConnection;
import com.warxim.petep.extension.internal.history.model.HistoricInterceptor;
import com.warxim.petep.extension.internal.history.model.HistoricPdu;
import com.warxim.petep.extension.internal.history.model.HistoricProxy;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.module.InterceptorModule;
import com.warxim.petep.interceptor.worker.Interceptor;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * History interceptor that serializes PDU to HistoricPdu and sends it to service for storage.
 */
public class HistoryInterceptor extends Interceptor {
    private final HistoryApi historyApi;

    /**
     * Constructs history interceptor.
     * @param id Identifier of interceptor (index of the interceptor)
     * @param module Parent module of the interceptor
     * @param helper Helper for accessing running instance of PETEP core
     * @param historyApi History API for saving the intercepted PDUs to the history database
     */
    public HistoryInterceptor(int id, InterceptorModule module, PetepHelper helper, HistoryApi historyApi) {
        super(id, module, helper);
        this.historyApi = historyApi;
    }

    @Override
    public boolean prepare() {
        return true;
    }

    @Override
    public boolean intercept(PDU pdu) {
        if (pdu.hasTag("no_history") && !pdu.hasTag("history")) {
            return true;
        }

        var data = new byte[pdu.getSize()];
        System.arraycopy(pdu.getBuffer(), 0, data, 0, data.length);

        Set<String> tags = pdu.getTags().isEmpty()
                ? Collections.emptySet()
                : new HashSet<>(pdu.getTags());
        var builder = HistoricPdu.builder()
                .proxy(HistoricProxy.builder()
                        .code(pdu.getProxy().getModule().getCode())
                        .name(pdu.getProxy().getModule().getName())
                        .build())
                .connection(HistoricConnection.builder()
                        .code(pdu.getConnection().getCode())
                        .name(pdu.getConnection().toString())
                        .build())
                .destination(pdu.getDestination())
                .interceptor(HistoricInterceptor.builder()
                        .code(module.getCode())
                        .name(module.getName())
                        .build())
                .tags(tags)
                .size(data.length)
                .time(Instant.now())
                .charset(pdu.getCharset())
                .data(data);

        var serializer = pdu.getProxy().getModule().getFactory().getSerializer();
        builder.metadata(serializer.serializePduMetadata(pdu));

        historyApi.getService().savePdu(builder.build());
        return true;
    }

    @Override
    public void stop() {
        // There is no need for action
    }
}
