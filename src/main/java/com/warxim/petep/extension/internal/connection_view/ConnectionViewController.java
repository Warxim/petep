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

import java.net.URL;
import java.util.ResourceBundle;
import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.proxy.worker.Proxy;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/** Connection View controller. */
public final class ConnectionViewController implements Initializable {
  /** Target proxy. */
  private final Proxy proxy;

  @FXML
  private TableView<Connection> table;
  @FXML
  private TableColumn<Connection, Integer> idColumn;
  @FXML
  private TableColumn<Connection, String> infoColumn;

  /** Connection View controller constructor. */
  public ConnectionViewController(Proxy proxy) {
    this.proxy = proxy;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    refresh();

    createContextMenu();
  }

  private void createContextMenu() {
    MenuItem killItem = new MenuItem("Kill");
    killItem.setOnAction(this::onKillConnectionClick);

    ContextMenu menu = new ContextMenu();
    menu.getItems().add(killItem);
    table.setContextMenu(menu);
  }

  private void onKillConnectionClick(ActionEvent event) {
    Connection connection = table.getSelectionModel().getSelectedItem();

    if (connection == null) {
      return;
    }

    connection.stop();
    refresh();
  }

  public void refresh() {
    idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    infoColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().toString()));

    table.setItems(FXCollections.observableArrayList(proxy.getConnectionManager().getList()));
  }

  public void killAll() {
    proxy.getConnectionManager().getList().forEach(Connection::stop);

    refresh();
  }
}
