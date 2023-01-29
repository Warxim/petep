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
package com.warxim.petep.gui.common;

import com.warxim.petep.extension.PetepAPI;

/**
 * GUI constants.
 * <p>Contains order number of internal tabs. (This can be used to add extension tab somewhere between them.)</p>
 */
@PetepAPI
public final class GuiConstant {
    /**
     * Path to main CSS file of PETEP application.
     */
    public static final String MAIN_CSS_PATH = "/css/Main.css";
    public static final String ICON_PATH = "/img/Logo.png";

    public static final Integer LOG_TAB_ORDER = -10000;
    public static final Integer SETTINGS_TAB_ORDER = -9000;
    public static final Integer HISTORY_TAB_ORDER = -8000;
    public static final Integer CONNECTIONS_TAB_ORDER = -7000;
    public static final Integer TAGGER_TAB_ORDER = -6000;
    public static final Integer MODIFIER_TAB_ORDER = -5000;
    public static final Integer SCRIPTER_TAB_ORDER = -4000;
    public static final Integer REPEATER_TAB_ORDER = -3000;

    public static final Integer CATCHER_TAB_ORDER = 10000;

    public static final Integer SETTINGS_EHTTPP_TAB_ORDER = 10000;
    public static final Integer SETTINGS_PROXIES_TAB_ORDER = -10000;
    public static final Integer SETTINGS_INTERCEPTORS_C2S_TAB_ORDER = -9000;
    public static final Integer SETTINGS_INTERCEPTORS_S2C_TAB_ORDER = -8000;

    private GuiConstant() {
    }
}
