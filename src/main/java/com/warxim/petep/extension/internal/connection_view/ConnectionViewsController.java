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
package com.warxim.petep.extension.internal.connection_view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.proxy.worker.Proxy;
import com.warxim.petep.util.GuiUtils;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/** Connection views controller. */
public final class ConnectionViewsController implements Initializable {
  private final List<ConnectionViewController> controllers;

  @FXML
  private TabPane tabs;
  @FXML
  private Button refreshButton;

  public ConnectionViewsController() {
    controllers = new ArrayList<>();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    refreshButton.setDisable(true);
    tabs.setVisible(false);

    MenuItem killAllItem = new MenuItem("Kill All");
    killAllItem.setOnAction(this::onKillAllClick);

    ContextMenu menu = new ContextMenu();
    menu.getItems().add(killAllItem);
    tabs.setContextMenu(menu);
  }

  public void load(PetepHelper helper) {
    // Create connection view tab for every proxy and its connection manager.
    for (Proxy proxy : helper.getProxies()) {
      try {
        FXMLLoader fxmlLoader = new FXMLLoader(
            getClass().getResource("/fxml/extension/internal/connection_view/ConnectionView.fxml"));

        ConnectionViewController controller = new ConnectionViewController(proxy);
        controllers.add(controller);
        fxmlLoader.setController(controller);

        GuiUtils.addTabToTabPane(tabs, proxy.getModule().getName(), fxmlLoader.load());
      } catch (IOException e) {
        Logger.getGlobal().log(Level.SEVERE, "Could not load connection view tab!", e);
      }
    }

    // Refresh connection view of proxy when its tab gets selected.
    tabs.getSelectionModel().selectedItemProperty().addListener(this::onTabSelectionChange);

    refreshButton.setDisable(false);
    tabs.setVisible(true);
  }

  public void unload() {
    refreshButton.setDisable(true);
    tabs.setVisible(false);
    controllers.clear();
    tabs.getTabs().clear();
  }

  private void onTabSelectionChange(
      ObservableValue<? extends Tab> observable,
      Tab oldTab,
      Tab newTab) {
    int index = tabs.getTabs().indexOf(newTab);
    if (index != -1) {
      controllers.get(index).refresh();
    }
  }

  private void onKillAllClick(ActionEvent event) {
    int index = tabs.getSelectionModel().getSelectedIndex();
    if (index != -1) {
      controllers.get(index).killAll();
    }
  }

  @FXML
  private void onRefreshButtonClick(ActionEvent event) {
    for (ConnectionViewController controller : controllers) {
      controller.refresh();
    }
  }
}
