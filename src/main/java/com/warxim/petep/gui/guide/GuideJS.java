/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2020 Michal Válka
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
package com.warxim.petep.gui.guide;

import com.warxim.petep.gui.GuiBundle;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GuideJS is API for JavaScript that allows it to use useful Java functions.
 */
public final class GuideJS {
    /**
     * Opens link in default desktop web browser.
     * @param url URL to be opened
     */
    public void openLink(String url) {
        try {
            GuiBundle.getInstance().getHostServices().showDocument(url);
        } catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, () -> String.format("Could not open URL '%s'!", url));
        }
    }
}
