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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.warxim.petep.Bundle;
import com.warxim.petep.core.PetepState;
import com.warxim.petep.core.listener.PetepListener;
import com.warxim.petep.gui.GuiBundle;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.factory.InterceptorModuleFactory;
import com.warxim.petep.interceptor.module.InterceptorModule;
import com.warxim.petep.proxy.factory.ProxyModuleFactory;
import com.warxim.petep.proxy.module.ProxyModule;
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

/** Settings controller. */
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

  /** Registers new tab to settings tab. */
  public void registerTab(String title, Node node) {
    GuiUtils.addTabToTabPane(tabs, title, node);
  }

  /** Unregisters tab from settings tab. */
  public void unregisterTab(Node node) {
    GuiUtils.removeTabFromTabPane(tabs, node);
  }

  private void loadProxySettings() {
    try {
      FXMLLoader fxmlLoader =
          new FXMLLoader(getClass().getResource("/fxml/tab/settings/ModuleSettings.fxml"));

      fxmlLoader.setController(new ModuleSettingsController<ProxyModule, ProxyModuleFactory>(
          "Proxies", Bundle.getInstance().getProxyModuleFactoryManager(),
          Bundle.getInstance().getProxyModuleContainer()));

      registerTab("Proxies", fxmlLoader.load());
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Could not add proxies tab", e);
    }
  }

  private void loadInterceptorC2SSettings() {
    try {
      FXMLLoader fxmlLoader =
          new FXMLLoader(getClass().getResource("/fxml/tab/settings/ModuleSettings.fxml"));

      fxmlLoader
          .setController(new ModuleSettingsController<InterceptorModule, InterceptorModuleFactory>(
              "Interceptors C2S (client → server)",
              Bundle.getInstance().getInterceptorModuleFactoryManager(),
              Bundle.getInstance().getInterceptorModuleContainerC2S()));

      registerTab("Interceptors C2S", fxmlLoader.load());
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Could not add interceptors C2S tab", e);
    }
  }

  private void loadInterceptorS2CSettings() {
    try {
      FXMLLoader fxmlLoader =
          new FXMLLoader(getClass().getResource("/fxml/tab/settings/ModuleSettings.fxml"));

      fxmlLoader
          .setController(new ModuleSettingsController<InterceptorModule, InterceptorModuleFactory>(
              "Interceptors S2C (server → client)",
              Bundle.getInstance().getInterceptorModuleFactoryManager(),
              Bundle.getInstance().getInterceptorModuleContainerS2C()));

      registerTab("Interceptors S2C", fxmlLoader.load());
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Could not add interceptors S2C tab", e);
    }
  }

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

  /** Locks settings. */
  private void lock() {
    this.tabs.setDisable(true);
  }

  /** Unlocks settings. */
  private void unlock() {
    this.tabs.setDisable(false);
  }

  @Override
  public void afterStart(PetepHelper helper) {
    Platform.runLater(() -> statusLabel.setText("STARTED"));
  }

  @Override
  public void afterStop(PetepHelper helper) {
    // Unlock GUI
    Platform.runLater(() -> {
      unlock();
      statusLabel.setText("STOPPED");
      startStopButton.setText("START");
    });
  }

  @Override
  public void afterPrepare(PetepHelper helper) {
    Platform.runLater(() -> statusLabel.setText("PREPARED"));
  }
}
