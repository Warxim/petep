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

import com.warxim.petep.core.listener.ListenerManager;
import com.warxim.petep.extension.internal.history.model.HistoricPdu;
import com.warxim.petep.extension.internal.history.model.HistoricPduView;

import java.util.Collection;

/**
 * Manager of history listeners, which uses {@link ListenerManager} implementation for keeping weak references to listeners.
 */
public class HistoryListenerManager extends ListenerManager<HistoryListener> implements HistoryListener {
    @Override
    public void onHistoricPduCreate(HistoricPdu pdu, HistoricPduView pduView) {
        parallelCall(listener -> listener.onHistoricPduCreate(pdu, pduView));
    }

    @Override
    public void onHistoricPduDelete(Collection<Long> ids) {
        parallelCall(listener -> listener.onHistoricPduDelete(ids));
    }

    @Override
    public void onHistoryClear() {
        parallelCall(HistoryListener::onHistoryClear);
    }
}
