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
package com.warxim.petep.extension.internal.catcher;

import com.warxim.petep.extension.Extension;
import com.warxim.petep.gui.common.GuiConstant;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.GuiHelper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Catcher extension.
 */
public final class CatcherExtension extends Extension {
    private CatcherController controller;
    private ExtensionHelper extensionHelper;

    /**
     * Catcher extension constructor.
     * @param path Path to the extension
     */
    public CatcherExtension(String path) {
        super(path);
    }

    @Override
    public void init(ExtensionHelper helper) {
        // Register PETEP listener if GUI is enabled.
        helper.registerInterceptorModuleFactory(new CatcherInterceptorModuleFactory(this));
        this.extensionHelper = helper;
    }

    @Override
    public void initGui(GuiHelper helper) {
        helper.registerGuide(new CatcherGuide());

        // Load catcher tab.
        try {
            var fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/extension/internal/catcher/Catcher.fxml"));

            controller = new CatcherController(extensionHelper);

            fxmlLoader.setController(controller);

            var node = (Node) fxmlLoader.load();

            helper.registerTab("Catcher", node, GuiConstant.CATCHER_TAB_ORDER);
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not load catcher GUI!", e);
        }
    }

    @Override
    public String getCode() {
        return "catcher";
    }

    @Override
    public String getName() {
        return "Catcher";
    }

    @Override
    public String getDescription() {
        return "Catches PDUs and allows user to edit them before relasing (manual intercepting).";
    }

    @Override
    public String getVersion() {
        return "1.2";
    }

    public CatcherController getController() {
        return controller;
    }
}
