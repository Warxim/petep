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
package com.warxim.petep.gui.controller.settings;

import com.warxim.petep.Bundle;
import com.warxim.petep.core.PetepState;
import com.warxim.petep.core.listener.PetepListener;
import com.warxim.petep.gui.GuiBundle;
import com.warxim.petep.gui.common.GuiConstant;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.util.GuiUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Settings controller.
 */
public final class SettingsController implements Initializable, PetepListener {
    @FXML
    private TabPane tabs;
    @FXML
    private Label statusLabel;
    @FXML
    private Button startStopButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Register settings controller to GUI bundle.
        GuiBundle.getInstance().setSettingsController(this);

        // Load proxy settings tab.
        loadProxySettings();
        loadInterceptorC2SSettings();
        loadInterceptorS2CSettings();

        // Register PETEP listener.
        Bundle.getInstance().getPetepListenerManager().registerListener(this);
    }

    /**
     * Registers new tab to settings tab.
     * @param title Title of the tab
     * @param node Node to be added as a child into the tab
     */
    public void registerTab(String title, Node node) {
        GuiUtils.addTabToTabPane(tabs, title, node);
    }

    /**
     * Registers new tab to settings tab.
     * @param title Title of the tab
     * @param node Node to be added as a child into the tab
     * @param order Order of the tab (where should the tab be placed)
     */
    public void registerTab(String title, Node node, Integer order) {
        GuiUtils.addTabToTabPane(tabs, title, node, order);
    }

    /**
     * Unregisters tab from settings tab.
     * @param node Child node of the tab that should be removed
     */
    public void unregisterTab(Node node) {
        GuiUtils.removeTabFromTabPane(tabs, node);
    }

    @Override
    public void afterCoreStart(PetepHelper helper) {
        Platform.runLater(() -> statusLabel.setText("STARTED"));
    }

    @Override
    public void afterCoreStop(PetepHelper helper) {
        // Unlock GUI
        Platform.runLater(() -> {
            unlock();
            statusLabel.setText("STOPPED");
            startStopButton.setText("START");
        });
    }

    @Override
    public void afterCorePrepare(PetepHelper helper) {
        Platform.runLater(() -> statusLabel.setText("PREPARED"));
    }

    /**
     * Starts or stops PETEP core.
     */
    @FXML
    private void onStartStopButtonClick(ActionEvent e) {
        if (Bundle.getInstance().getPetepManager().getState() == PetepState.STOPPED) {
            // Lock GUI
            lock();
            Platform.runLater(() -> statusLabel.setText("STARTING..."));
            startStopButton.setText("STOP");

            Bundle.getInstance().getPetepManager().start();
        } else {
            Bundle.getInstance().getPetepManager().stop();
        }
    }

    /**
     * Loads proxy settings tab.
     */
    private void loadProxySettings() {
        try {
            var fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/tab/settings/ModuleSettings.fxml"));

            fxmlLoader.setController(new ModuleSettingsController<>(
                    "Proxies", Bundle.getInstance().getProxyModuleFactoryManager(),
                    Bundle.getInstance().getProxyModuleContainer()));

            registerTab("Proxies", fxmlLoader.load(), GuiConstant.SETTINGS_PROXIES_TAB_ORDER);
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not add proxies tab", e);
        }
    }

    /**
     * Loads interceptor C2S settings tab.
     */
    private void loadInterceptorC2SSettings() {
        try {
            var fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/tab/settings/ModuleSettings.fxml"));

            fxmlLoader
                    .setController(new ModuleSettingsController<>(
                            "Interceptors C2S (client → server)",
                            Bundle.getInstance().getInterceptorModuleFactoryManager(),
                            Bundle.getInstance().getInterceptorModuleContainerC2S()));

            registerTab("Interceptors C2S", fxmlLoader.load(), GuiConstant.SETTINGS_INTERCEPTORS_C2S_TAB_ORDER);
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not add interceptors C2S tab", e);
        }
    }

    /**
     * Loads interceptor S2C settings tab.
     */
    private void loadInterceptorS2CSettings() {
        try {
            var fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/tab/settings/ModuleSettings.fxml"));

            fxmlLoader
                    .setController(new ModuleSettingsController<>(
                            "Interceptors S2C (server → client)",
                            Bundle.getInstance().getInterceptorModuleFactoryManager(),
                            Bundle.getInstance().getInterceptorModuleContainerS2C()));

            registerTab("Interceptors S2C", fxmlLoader.load(), GuiConstant.SETTINGS_INTERCEPTORS_S2C_TAB_ORDER);
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not add interceptors S2C tab", e);
        }
    }

    /**
     * Locks settings.
     */
    private void lock() {
        this.tabs.setDisable(true);
    }

    /**
     * Unlocks settings.
     */
    private void unlock() {
        this.tabs.setDisable(false);
    }
}
