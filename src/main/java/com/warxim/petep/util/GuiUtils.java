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
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

/**
 * GUI utils.
 */
@PetepAPI
public final class GuiUtils {
    private GuiUtils() {
    }

    /**
     * Adds tab(node) to specified tab pane.
     * (Wraps the node to a scroll pane, creates new tab and adds it to the tabPane.)
     * @param tabPane Tab pane to which we want to add the tab
     * @param title Text of the tab title
     * @param node Content node, which will be a content of the tab
     * @return Created tab
     */
    public static Tab addTabToTabPane(TabPane tabPane, String title, Node node) {
        var scrollPane = new ScrollPane(node);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        var tab = new Tab(title, scrollPane);
        tabPane.getTabs().add(tab);
        return tab;
    }

    /**
     * Adds tab(node) to specified tab pane using the specified order.
     * (Wraps the node to a scroll pane, creates new tab and adds it to the tabPane.)
     * @param tabPane Tab pane to which to add the tab
     * @param title Text of the tab title
     * @param node Content node, which will be a content of the tab
     * @param order Order to use when adding the tab
     * @return Created tab
     */
    public static Tab addTabToTabPane(TabPane tabPane, String title, Node node, Integer order) {
        var scrollPane = new ScrollPane(node);

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        var tab = new Tab(title, scrollPane);
        tab.setUserData(order);

        var tabs = tabPane.getTabs();
        int index = 0;
        for (; index < tabs.size(); ++index) {
            var currentOrder = tabs.get(index).getUserData();
            if (currentOrder == null || (Integer) currentOrder > order) {
                break;
            }
        }
        tabPane.getTabs().add(index, tab);
        return tab;
    }

    /**
     * Removes tab from specified tab pane.
     * @param tabPane Tab pane from which to remove the tab
     * @param node Content node of the tab, which should be removed
     */
    public static void removeTabFromTabPane(TabPane tabPane, Node node) {
        for (Tab tab : tabPane.getTabs()) {
            Node content = tab.getContent();
            if (content instanceof ScrollPane && ((ScrollPane) content).getContent() == node) {
                tabPane.getTabs().remove(tab);
                break;
            }
        }
    }

    /**
     * Removes text formatting that has been done for JavaFx controls.
     * <p>Replaces special unicode replacement charters with original characters for displaying them.</p>
     * @param text Text to unformat
     * @return Unformated text
     */
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

    /**
     * Formats text so it can be displayed inside JavaFX controls.
     * <p>Replaces special characters with their unicode replacement for displaying them.</p>
     * @param text Text to format
     * @return Formated text
     */
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

    /**
     * Creates tooltip with the specified text and default show duration.
     * @param text Text to show in tooltip
     * @return Tooltip
     */
    public static Tooltip createTooltip(String text) {
        return createTooltip(text, 250);
    }

    /**
     * Creates tooltip with the specified text and default show delay.
     * @param text Text to show in tooltip
     * @param millisDuration After how many milliseconds to hide the tooltip
     * @return Tooltip
     */
    public static Tooltip createTooltip(String text, int millisDuration) {
        var tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.millis(millisDuration));
        return tooltip;
    }
}
