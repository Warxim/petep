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
package com.warxim.petep.extension.internal.history.gui;

import com.warxim.petep.extension.internal.history.HistoryApi;
import com.warxim.petep.extension.internal.history.gui.view.HistoryView;
import com.warxim.petep.extension.internal.history.model.HistoryFilter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for history tab.
 * <p>
 *     Displays history view.
 * </p>
 */
public final class HistoryController implements Initializable {
    @FXML
    private AnchorPane viewPane;

    private HistoryView view;

    /**
     * Constructs history controller.
     * @param historyApi History API for creation of history view
     * @param filter Filter to use in the view
     */
    public HistoryController(HistoryApi historyApi, HistoryFilter filter) {
        view = historyApi.createView(filter);
    }

    /**
     * Obtains currently used filter.
     * @return Currently used filter from the underlying view
     */
    public HistoryFilter getFilter() {
        return view.getFilter();
    }

    /**
     * Sets new filter.
     * @param filter Filter for the underlying view
     */
    public void setFilter(HistoryFilter filter) {
        view.setFilter(filter);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var node = view.getNode();
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        viewPane.getChildren().add(node);
    }
}
