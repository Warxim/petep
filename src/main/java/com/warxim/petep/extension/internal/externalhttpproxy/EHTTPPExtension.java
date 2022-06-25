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
package com.warxim.petep.extension.internal.externalhttpproxy;

import com.warxim.petep.core.listener.PetepListener;
import com.warxim.petep.core.pdu.PduQueue;
import com.warxim.petep.extension.Extension;
import com.warxim.petep.gui.common.GuiConstant;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.GuiHelper;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.worker.Interceptor;
import com.warxim.petep.persistence.Storable;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * External HTTP proxy extension.
 */
public final class EHTTPPExtension extends Extension implements Storable<EHTTPPConfig>, PetepListener {
    private EHTTPPManager manager;
    private EHTTPPConfig config;

    /**
     * External HTTP proxy extension constructor.
     * @param path Path to the extension
     */
    public EHTTPPExtension(String path) {
        super(path);
        manager = null;
        Logger.getGlobal().info("HTTP Proxy extension loaded.");
    }

    @Override
    public void init(ExtensionHelper helper) {
        Logger.getGlobal().info("HTTP Proxy extension registered.");

        if (config == null) {
            // Use defaults.
            config = new EHTTPPConfig("127.0.0.1", 8181, "127.0.0.1", 8080);
        }

        helper.registerPetepListener(this);

        helper.registerInterceptorModuleFactory(new EHTTPPInterceptorModuleFactory(this));
    }

    @Override
    public void initGui(GuiHelper helper) {
        try {
            var fxmlLoader = new FXMLLoader(
                    getClass().getResource("/fxml/extension/internal/ehttpp/HTTPPSettings.fxml")
            );
            fxmlLoader.setController(new EHTTPPSettingsController(config));
            helper.registerSettingsTab("HTTP Proxy", fxmlLoader.load(), GuiConstant.SETTINGS_EHTTPP_TAB_ORDER);
            Logger.getGlobal().info("HTTP Proxy extension GUI registered.");
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "HTTP Proxy tab could not be created.", e);
        }

        helper.registerGuide(new EHTTPPGuide());
    }

    @Override
    public String getCode() {
        return "external_http_proxy";
    }

    @Override
    public String getName() {
        return "External HTTP Proxy";
    }

    @Override
    public String getDescription() {
        return "Internal extension for external HTTP proxy usage.";
    }

    @Override
    public String getVersion() {
        return "1.2";
    }

    @Override
    public EHTTPPConfig saveStore() {
        return config;
    }

    @Override
    public void loadStore(EHTTPPConfig store) {
        this.config = store;
    }

    @Override
    public void beforeCorePrepare(PetepHelper helper) {
        processInterceptors(helper.getInterceptorsC2S());
        processInterceptors(helper.getInterceptorsS2C());

        if (manager != null) {
            manager.start(helper);
        }
    }

    @Override
    public void beforeCoreStop(PetepHelper helper) {
        if (manager != null) {
            manager.stop();
            manager = null;
        }
    }

    /**
     * Creates HTTP client for each EHTTPP interceptor.
     */
    private void processInterceptors(List<Interceptor> interceptors) {
        for (var interceptor : interceptors) {
            if (!(interceptor instanceof EHTTPPInterceptor)) {
                continue;
            }

            if (manager == null) {
                manager = new EHTTPPManager(config);
            }

            var queue = new PduQueue();

            ((EHTTPPInterceptor) interceptor).setQueue(queue);

            // Create HTTPP client for interceptor queue and set the following interceptor as a target
            manager.createClient(queue, interceptor.getId() + 1);
        }
    }
}
