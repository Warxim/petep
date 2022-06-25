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
package com.warxim.petep.extension.internal.repeater;

import com.warxim.petep.common.ContextType;
import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.internal.history.HistoryApi;
import com.warxim.petep.extension.internal.repeater.config.RepeaterConfig;
import com.warxim.petep.extension.internal.repeater.gui.RepeaterController;
import com.warxim.petep.extension.internal.repeater.gui.RepeaterReceiver;
import com.warxim.petep.gui.common.GuiConstant;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.GuiHelper;
import com.warxim.petep.persistence.Storable;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repeater extension
 * <p>Adds support for repeating PDUs using GUI editor.</p>
 */
public class RepeaterExtension extends Extension implements Storable<RepeaterConfig> {
    private ExtensionHelper extensionHelper;
    private RepeaterController controller;
    private HistoryApi historyApi;
    private RepeaterConfig config;
    private RepeaterReceiver receiver;

    /**
     * Constructs repeater extension.
     * @param path Path to the extension
     */
    public RepeaterExtension(String path) {
        super(path);
    }

    @Override
    public void beforeInit(ExtensionHelper helper) {
        if (helper.getContextType() != ContextType.GUI) {
            // Repeater works only in GUI mode
            return;
        }

        this.extensionHelper = helper;

        receiver = new RepeaterReceiver();
        helper.registerReceiver(receiver);
    }

    @Override
    public void init(ExtensionHelper helper) {
        if (helper.getContextType() != ContextType.GUI) {
            // Repeater works only in GUI mode
            return;
        }

        historyApi = (HistoryApi) helper.getExtension("history").orElse(null);
    }

    @Override
    public void initGui(GuiHelper helper) {
        try {
            // Create repeater tab and register it to the application tabs
            var fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/extension/internal/repeater/Repeater.fxml"));
            controller = new RepeaterController(extensionHelper, historyApi);
            extensionHelper.registerPetepListener(controller);
            fxmlLoader.setController(controller);
            helper.registerTab("Repeater", fxmlLoader.load(), GuiConstant.REPEATER_TAB_ORDER);
            if (config != null) {
                controller.init(config);
            }
            receiver.setController(controller);
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not load repeater!", e);
        }

        helper.registerGuide(new RepeaterGuide());
    }

    @Override
    public String getCode() {
        return "repeater";
    }

    @Override
    public String getName() {
        return "Repeater";
    }

    @Override
    public String getDescription() {
        return "Repeater is an extension for repeating PDUs.";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public RepeaterConfig saveStore() {
        if (controller == null) {
            return config;
        }
        config = controller.save();
        return config;
    }

    @Override
    public void loadStore(RepeaterConfig config) {
        this.config = config;
    }

    @Override
    public void destroy() {
        if (controller != null) {
            controller.destroy();
        }
    }
}
