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

import java.util.ArrayList;
import java.util.List;
import com.warxim.petep.gui.guide.internal.BasicsGuide;
import com.warxim.petep.gui.guide.internal.DiagramGuide;
import com.warxim.petep.gui.guide.internal.IntroductionGuide;
import com.warxim.petep.gui.guide.internal.TipsAndTricksGuide;

/** Manages guides. */
public final class GuideManager {
  private final List<Guide> guides;

  public GuideManager() {
    guides = new ArrayList<>();

    // Load internal guides.
    add(new IntroductionGuide());
    add(new BasicsGuide());
    add(new TipsAndTricksGuide());
    add(new DiagramGuide());
  }

  /** Adds guide to the manager. */
  public synchronized boolean add(Guide guide) {
    return guides.add(guide);
  }

  /** Removes guide from the manager. */
  public synchronized boolean remove(Guide guide) {
    return guides.remove(guide);
  }

  /** Returns list of guides. */
  public List<Guide> getList() {
    return guides;
  }
}
