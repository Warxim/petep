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

import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.extension.internal.history.model.HistoryFilter;
import javafx.scene.Node;

/**
 * History view is component for rendering PDU history in GUI.
 * <p>History view is automatically connected with the extension and refreshed.</p>
 * <p>After the view is not needed, destroy should be called, so that listeners are correctly unregistered.</p>
 */
@PetepAPI
public interface HistoryView {
    /**
     * Get JavaFX node for history view.
     * @return JavaFX node that contains the whole HistoryView (table with historic items, PDU view, filter)
     */
    Node getNode();

    /**
     * Get history filter.
     * @return Currently used HistoryFilter that filters items, which are added to the view
     */
    HistoryFilter getFilter();

    /**
     * Sets history filter.
     * <p>This automatically refreshes the items, so that there are all items matching the filter.</p>
     * @param filter New HistoryFilter to be used for filtering items, which are added to the view
     */
    void setFilter(HistoryFilter filter);

    /**
     * Destroys the view.
     * <p>This method should be called after the view is not needed anymore, because it handles unregistration of listeners etc.</p>
     */
    void destroy();
}
