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
package com.warxim.petep.extension.internal.history;

import com.warxim.petep.extension.Extension;
import com.warxim.petep.extension.internal.history.exception.HistoryExtensionException;
import com.warxim.petep.extension.internal.history.gui.HistoryController;
import com.warxim.petep.extension.internal.history.gui.view.DefaultHistoryView;
import com.warxim.petep.extension.internal.history.gui.view.HistoryView;
import com.warxim.petep.extension.internal.history.interceptor.HistoryInterceptorModuleFactory;
import com.warxim.petep.extension.internal.history.listener.HistoryListener;
import com.warxim.petep.extension.internal.history.listener.HistoryListenerManager;
import com.warxim.petep.extension.internal.history.model.HistoryFilter;
import com.warxim.petep.extension.internal.history.repository.CachedDatabaseHistoryRepository;
import com.warxim.petep.extension.internal.history.service.CachedHistoryService;
import com.warxim.petep.extension.internal.history.service.DefaultHistoryService;
import com.warxim.petep.extension.internal.history.service.HistoryService;
import com.warxim.petep.gui.common.GuiConstant;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.GuiHelper;
import com.warxim.petep.persistence.Storable;
import com.warxim.petep.util.FileUtils;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * History extension
 * <p>Adds support for storing intercepted traffic to database and allows user to query over it through GUI.</p>
 * <p>Can be used by other extensions using {@link HistoryApi}.</p>
 */
public class HistoryExtension extends Extension implements HistoryApi, Storable<HistoryConfig> {
    private HistoryConfig config;
    private HistoryController controller;
    private DefaultHistoryService service;
    private HistoryListenerManager listenerManager;
    private ExtensionHelper extensionHelper;

    /**
     * Constructs history extension.
     * @param path Path to the extension
     */
    public HistoryExtension(String path) {
        super(path);
    }

    @Override
    public void init(ExtensionHelper helper) {
        try {
            // Create repository and service
            var repository = new CachedDatabaseHistoryRepository("jdbc:sqlite:" + FileUtils.getProjectFile("history.db").getAbsolutePath());
            listenerManager = new HistoryListenerManager();
            service = new CachedHistoryService(repository, listenerManager);

            // Register interceptor factory for storing historic PDUs
            helper.registerInterceptorModuleFactory(new HistoryInterceptorModuleFactory(this, this));

            this.extensionHelper = helper;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new HistoryExtensionException("Could not load history database!", exception);
        } catch (ExecutionException | SQLException | RuntimeException exception) {
            throw new HistoryExtensionException("Could not load history database!", exception);
        }
    }

    @Override
    public void initGui(GuiHelper helper) {
        try {
            // Load history tab with history view
            var fxmlLoader = new FXMLLoader(
                    getClass().getResource("/fxml/extension/internal/history/History.fxml")
            );
            var filter = (config != null && config.getFilter() != null)
                    ? config.getFilter()
                    : new HistoryFilter();
            controller = new HistoryController(this, filter);
            fxmlLoader.setController(controller);

            helper.registerTab("History", fxmlLoader.load(), GuiConstant.HISTORY_TAB_ORDER);
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not load history tab!", e);
        }

        helper.registerGuide(new HistoryGuide());
    }

    @Override
    public void destroy() {
        service.stop();
    }

    @Override
    public String getCode() {
        return "history";
    }

    @Override
    public String getName() {
        return "History";
    }

    @Override
    public String getDescription() {
        return "Creates SQLite database and persists PDUs.";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public HistoryView createView(HistoryFilter filter) {
        return new DefaultHistoryView(this, extensionHelper, filter);
    }

    @Override
    public HistoryService getService() {
        return service;
    }

    @Override
    public void registerListener(HistoryListener listener) {
        listenerManager.registerListener(listener);
    }

    @Override
    public void unregisterListener(HistoryListener listener) {
        listenerManager.unregisterListener(listener);
    }

    @Override
    public HistoryConfig saveStore() {
        if (controller == null) {
            return config;
        }
        return HistoryConfig.builder()
                .filter(controller.getFilter())
                .build();
    }

    @Override
    public void loadStore(HistoryConfig store) {
        this.config = store;
    }
}
