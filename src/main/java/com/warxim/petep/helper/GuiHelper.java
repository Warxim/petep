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
package com.warxim.petep.helper;

import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.gui.guide.Guide;
import javafx.scene.Node;
import javafx.scene.image.Image;

/**
 * Helper for extensions with GUI that allows extensions to use internal GUI components, register
 * GUI components etc.
 */
@PetepAPI
public interface GuiHelper {
  /*
   * APPLICATION TABS
   */
  void registerTab(String title, Node node);

  void unregisterTab(Node node);

  /*
   * SETTINGS
   */
  void registerSettingsTab(String title, Node node);

  void unregisterSettingsTab(Node node);

  /*
   * GUIDES
   */
  void registerGuide(Guide guide);

  void unregisterGuide(Guide guide);

  /*
   * OTHER
   */
  Image getPetepIcon();
}
