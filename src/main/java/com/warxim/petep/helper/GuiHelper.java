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
    /**
     * Registers tab (adds it to the main application tabs).
     * <p>Node will be automatically wrapped into {@link javafx.scene.control.ScrollPane} and added into {@link javafx.scene.control.Tab}.</p>
     * @param title Text displayed in the tab title
     * @param node Node to be added as child into the tab
     */
    void registerTab(String title, Node node);

    /**
     * Registers tab (adds it to the main application tabs).
     * <p>Node will be automatically wrapped into {@link javafx.scene.control.ScrollPane} and added into {@link javafx.scene.control.Tab}.</p>
     * @param title Text displayed in the tab title
     * @param node Node to be added as child into the tab
     * @param order Order of the tab (internal tabs use orders defined in {@link com.warxim.petep.gui.common.GuiConstant})
     */
    void registerTab(String title, Node node, Integer order);

    /**
     * Unregisters tab (removes it from the main application tabs).
     * <p>Automatically searches for tab with given node.</p>
     * @param node Node to be removed from the application tabs
     */
    void unregisterTab(Node node);

    /*
     * SETTINGS
     */
    /**
     * Registers tab (adds it to the settings tabs).
     * <p>Node will be automatically wrapped into {@link javafx.scene.control.ScrollPane} and added into {@link javafx.scene.control.Tab}.</p>
     * @param title Text displayed in the tab title
     * @param node Node to be added as child into the tab
     */
    void registerSettingsTab(String title, Node node);

    /**
     * Registers tab (adds it to the settings tabs).
     * <p>Node will be automatically wrapped into {@link javafx.scene.control.ScrollPane} and added into {@link javafx.scene.control.Tab}.</p>
     * @param title Text displayed in the tab title
     * @param node Node to be added as child into the tab
     * @param order Order of the tab (internal tabs use orders defined in {@link com.warxim.petep.gui.common.GuiConstant})
     */
    void registerSettingsTab(String title, Node node, Integer order);

    /**
     * Unregisters tab (removes it from the settings tabs).
     * <p>Automatically searches for tab with given node.</p>
     * @param node Node to be removed from the application tabs
     */
    void unregisterSettingsTab(Node node);

    /*
     * GUIDES
     */
    /**
     * Registers guide into application guides.
     * <p>Registered guide will be accessible through the application menu.</p>
     * @param guide Guide to register
     */
    void registerGuide(Guide guide);

    /**
     * Unregisters guide from application guides.
     * @param guide Guide to unregister
     */
    void unregisterGuide(Guide guide);

    /*
     * OTHER
     */
    /**
     * Obtains PETEP icon.
     * @return JavaFX Image with PETEP icon
     */
    Image getPetepIcon();
}
