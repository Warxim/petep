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
package com.warxim.petep.extension.internal.history.gui.view;

import com.warxim.petep.extension.internal.history.HistoryApi;
import com.warxim.petep.extension.internal.history.listener.HistoryListener;
import com.warxim.petep.extension.internal.history.model.HistoricPdu;
import com.warxim.petep.extension.internal.history.model.HistoricPduView;
import com.warxim.petep.extension.internal.history.model.HistoryFilter;
import com.warxim.petep.helper.ExtensionHelper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import lombok.Getter;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of HistoryView.
 */
@Getter
public class DefaultHistoryView implements HistoryView, HistoryListener {
    private HistoryApi api;
    private Node node;
    private HistoryFilter filter;
    private HistoryViewController controller;

    /**
     * Constructs default history view.
     * @param api History API for accessing the history database
     * @param extensionHelper Extension helper
     * @param filter History filter to apply on the history
     */
    public DefaultHistoryView(HistoryApi api, ExtensionHelper extensionHelper, HistoryFilter filter) {
        this.api = api;
        this.filter = filter;

        try {
            var fxmlLoader = new FXMLLoader(
                    getClass().getResource("/fxml/extension/internal/history/HistoryView.fxml")
            );
            controller = new HistoryViewController(this, api, extensionHelper);
            fxmlLoader.setController(controller);
            node = fxmlLoader.load();
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not load history view!", e);
        }

        api.registerListener(this);
    }

    @Override
    public void setFilter(HistoryFilter filter) {
        this.filter = filter;
        try {
            var items = api.getService().getPduViewsByFilter(filter).get();
            controller.setItems(items);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            Logger.getGlobal().log(Level.SEVERE, "Could not set HistoryView filter!", e);
        }
        controller.onFilterChange();
    }

    @Override
    public void destroy() {
        api.unregisterListener(this);
    }

    @Override
    public void onHistoricPduDelete(Collection<Long> ids) {
        controller.removeItems(ids);
    }

    @Override
    public void onHistoricPduCreate(HistoricPdu pdu, HistoricPduView pduView) {
        if (filter.matches(pdu)) {
            controller.addItem(pduView);
        }
    }

    @Override
    public void onHistoryClear() {
        controller.setItems(new LinkedList<>());
    }
}
