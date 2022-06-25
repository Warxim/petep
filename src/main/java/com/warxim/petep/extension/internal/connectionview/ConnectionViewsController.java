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
package com.warxim.petep.extension.internal.connectionview;

import com.warxim.petep.core.listener.PetepListener;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.util.GuiUtils;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Connection views controller that contains multiple connection view controllers.
 * <p>Contains tabs with connection view controllers, which manage connection for given proxies.</p>
 */
public final class ConnectionViewsController implements Initializable, PetepListener {
    private final List<ConnectionViewController> controllers;

    @FXML
    private TabPane tabs;
    @FXML
    private Button refreshButton;

    /**
     * Constructs controller for multiple connection views.
     */
    public ConnectionViewsController() {
        controllers = new ArrayList<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshButton.setDisable(true);
        tabs.setVisible(false);
    }

    /**
     * Creates tabs for proxies after PETEP core is prepared.
     */
    @Override
    public void afterCorePrepare(PetepHelper helper) {
        Platform.runLater(() -> {
            // Create connection view tab for every proxy and its connection manager.
            for (var proxy : helper.getProxies()) {
                try {
                    var fxmlLoader = new FXMLLoader(
                            getClass().getResource("/fxml/extension/internal/connectionview/ConnectionView.fxml")
                    );

                    var controller = new ConnectionViewController(proxy);
                    controllers.add(controller);
                    fxmlLoader.setController(controller);
                    helper.registerConnectionListener(controller);

                    var tab = GuiUtils.addTabToTabPane(tabs, proxy.getModule().getName(), fxmlLoader.load());
                    initTabContextMenu(tab);
                } catch (IOException e) {
                    Logger.getGlobal().log(Level.SEVERE, "Could not load connection view tab!", e);
                }
            }

            // Refresh connection view of proxy when its tab gets selected.
            tabs.getSelectionModel().selectedItemProperty().addListener(this::onTabSelectionChange);

            refreshButton.setDisable(false);
            tabs.setVisible(true);
        });
    }

    /**
     * Removes tabs and clears the GUI before PETEP stops.
     */
    @Override
    public void beforeCoreStop(PetepHelper helper) {
        Platform.runLater(() -> {
            refreshButton.setDisable(true);
            tabs.setVisible(false);
            controllers.forEach(ConnectionViewController::destroy);
            controllers.clear();
            tabs.getTabs().clear();
        });
    }

    /**
     * Refreshes all controllers.
     */
    @FXML
    private void onRefreshButtonClick(ActionEvent event) {
        for (var controller : controllers) {
            controller.refresh();
        }
    }

    /**
     * Initializes context menu for tab (adds "Kill All" item).
     */
    private void initTabContextMenu(Tab tab) {
        var killAllItem = new MenuItem("Kill All");
        killAllItem.setOnAction(event -> onKillAllClick(tab));
        var menu = new ContextMenu();
        menu.getItems().add(killAllItem);
        tab.setContextMenu(menu);
    }

    /**
     * When tab selection changes, refresh the opened tab - controller.
     */
    private void onTabSelectionChange(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
        var index = tabs.getTabs().indexOf(newTab);
        if (index != -1) {
            controllers.get(index).refresh();
        }
    }

    /**
     * On "Kill All" click kill all connections in the opened tab - controller.
     */
    private void onKillAllClick(Tab tab) {
        var index = tabs.getTabs().indexOf(tab);
        if (index != -1) {
            controllers.get(index).killAll();
        }
    }
}
