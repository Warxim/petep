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

import com.warxim.petep.gui.guide.internal.BasicsGuide;
import com.warxim.petep.gui.guide.internal.DiagramGuide;
import com.warxim.petep.gui.guide.internal.IntroductionGuide;
import com.warxim.petep.gui.guide.internal.TipsAndTricksGuide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages all guides.
 */
public final class GuideManager {
    private final List<Guide> guides;

    /**
     * Constructs guide manager.
     */
    public GuideManager() {
        guides = new ArrayList<>();

        // Load internal guides.
        add(new IntroductionGuide());
        add(new BasicsGuide());
        add(new TipsAndTricksGuide());
        add(new DiagramGuide());
    }

    /**
     * Adds guide to the manager.
     * @param guide Guide to be added
     * @return {@code true} if the guide was successfully added
     */
    public synchronized boolean add(Guide guide) {
        return guides.add(guide);
    }

    /**
     * Removes guide from the manager.
     * @param guide Guide to be removed
     * @return {@code true} if the guide was successfully removed (the manager contained the guide)
     */
    public synchronized boolean remove(Guide guide) {
        return guides.remove(guide);
    }

    /**
     * Obtains list of guides that are present in the manager.
     * @return Unmodifiable list of guides
     */
    public List<Guide> getList() {
        return Collections.unmodifiableList(guides);
    }
}
