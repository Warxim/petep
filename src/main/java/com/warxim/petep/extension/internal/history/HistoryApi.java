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
package com.warxim.petep.extension.internal.history;

import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.extension.internal.history.gui.view.HistoryView;
import com.warxim.petep.extension.internal.history.listener.HistoryListener;
import com.warxim.petep.extension.internal.history.model.HistoryFilter;
import com.warxim.petep.extension.internal.history.service.HistoryService;

/**
 * API for History extension
 */
@PetepAPI
public interface HistoryApi {
    /**
     * Creates history view for displaying history in JavaFX context.
     * @param filter Initial filter to use in history view
     * @return History view object for managing history view
     */
    HistoryView createView(HistoryFilter filter);

    /**
     * Gets history service for querying history.
     * @return History service
     */
    HistoryService getService();

    /**
     * Registers listener for handling history events.
     * <p>
     *     <b>Note:</b> Listeners are stored using weak references in history module.
     *     Strong reference has to be stored somewhere in the code.
     * </p>
     * @param listener Listener to be registered
     */
    void registerListener(HistoryListener listener);

    /**
     * Unregisters listener from the history extension.
     * @param listener Listener to be unregistered
     */
    void unregisterListener(HistoryListener listener);
}
