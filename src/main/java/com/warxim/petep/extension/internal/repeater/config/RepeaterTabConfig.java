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
package com.warxim.petep.extension.internal.repeater.config;

import com.warxim.petep.core.pdu.SerializedPdu;
import com.warxim.petep.extension.internal.history.model.HistoryFilter;
import lombok.Value;

/**
 * Configuration of repeater tab.
 */
@Value
public class RepeaterTabConfig {
    /**
     * Title of the tab
     */
    String title;
    /**
     * Serialized PDU displayed in the tab PDU editor
     */
    SerializedPdu serializedPdu;
    /**
     * History filter used in the tab history view
     */
    HistoryFilter historyFilter;
}
