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
package com.warxim.petep.util;

import com.warxim.petep.extension.PetepAPI;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/** GUI utils. */
@PetepAPI
public final class GuiUtils {
  private GuiUtils() {}

  /**
   * Adds tab(node) to specified tab pane. (Wraps the node to a scroll pane, creates neww tab and
   * adds it to the tabPane.)
   */
  public static void addTabToTabPane(TabPane tabPane, String title, Node node) {
    ScrollPane scrollPane = new ScrollPane(node);

    scrollPane.setFitToHeight(true);
    scrollPane.setFitToWidth(true);

    tabPane.getTabs().add(new Tab(title, scrollPane));
  }

  /** Removes tab from specified tab pane. */
  public static void removeTabFromTabPane(TabPane tabPane, Node node) {
    for (Tab tab : tabPane.getTabs()) {
      Node content = tab.getContent();
      if (content instanceof ScrollPane && ((ScrollPane) content).getContent() == node) {
        tabPane.getTabs().remove(tab);
        break;
      }
    }
  }

  /** Removes text formatting that has been done for JavaFx controls. */
  public static String unformatText(String text) {
    char c = '\u2400';
    for (int i = 0; i < 9; ++i) {
      text = text.replace(c++, (char) i);
    }

    c = '\u240B';
    for (int i = 11; i < 32; ++i) {
      text = text.replace(c++, (char) i);
    }

    text = text.replace('\u2421', (char) 0x7F);

    return text;
  }

  /** Formats text so it can be displayed inside JavaFX controls. */
  public static String formatText(String text) {
    char c = '\u2400';
    for (int i = 0; i < 9; ++i) {
      text = text.replace((char) i, c++);
    }

    c = '\u240B';
    for (int i = 11; i < 32; ++i) {
      text = text.replace((char) i, c++);
    }

    text = text.replace((char) 0x7F, '\u2421');

    return text;
  }
}
