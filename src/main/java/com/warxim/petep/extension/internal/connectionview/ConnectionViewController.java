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

import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.listener.ConnectionListener;
import com.warxim.petep.proxy.worker.Proxy;
import javafx.application.Platform;
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

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Connection View controller.
 */
public final class ConnectionViewController implements Initializable, ConnectionListener {
    /**
     * Target proxy.
     */
    private final Proxy proxy;

    @FXML
    private TableView<Connection> table;
    @FXML
    private TableColumn<Connection, String> codeColumn;
    @FXML
    private TableColumn<Connection, String> infoColumn;

    /**
     * Connection View controller constructor.
     * @param proxy Proxy for which to display connections
     */
    public ConnectionViewController(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refresh();

        createContextMenu();
    }

    @Override
    public void onConnectionStart(Connection connection) {
        if (!connection.getProxy().equals(proxy)) {
            return;
        }

        Platform.runLater(() -> table.getItems().add(connection));
    }

    @Override
    public void onConnectionStop(Connection connection) {
        if (!connection.getProxy().equals(proxy)) {
            return;
        }

        Platform.runLater(() -> table.getItems().remove(connection));
    }

    /**
     * Destroys the controller (removes connections from the table).
     */
    public void destroy() {
        table.getItems().clear();
    }

    /**
     * Refreshes the controller (refreshes table content).
     */
    public void refresh() {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        infoColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().toString()));

        table.setItems(FXCollections.observableArrayList(proxy.getConnectionManager().getList()));
    }

    /**
     * Kills all connections.
     */
    public void killAll() {
        proxy.getConnectionManager().getList().forEach(Connection::stop);

        refresh();
    }

    /**
     * Creates context menu for table (adds "Kill" item).
     */
    private void createContextMenu() {
        var killItem = new MenuItem("Kill");
        killItem.setOnAction(this::onKillConnectionClick);

        var menu = new ContextMenu();
        menu.getItems().add(killItem);
        table.setContextMenu(menu);
    }

    /**
     * Kills currently selected connection.
     */
    private void onKillConnectionClick(ActionEvent event) {
        var connection = table.getSelectionModel().getSelectedItem();
        if (connection == null) {
            return;
        }

        connection.stop();
        refresh();
    }
}
