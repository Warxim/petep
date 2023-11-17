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
package com.warxim.petep.extension.internal.history.gui.view;

import com.warxim.petep.common.Constant;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.core.pdu.SerializedPdu;
import com.warxim.petep.extension.internal.history.HistoryApi;
import com.warxim.petep.extension.internal.history.gui.view.filter.HistoryFilterDialog;
import com.warxim.petep.extension.internal.history.model.HistoricPduView;
import com.warxim.petep.gui.common.InstantCellFactory;
import com.warxim.petep.gui.control.byteseditor.BytesEditor;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.util.GuiUtils;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * History View Controller for controlling GUI elements.
 */
@RequiredArgsConstructor
public class HistoryViewController implements Initializable {
    private static final PseudoClass LABEL_PSEUDO_CLASS = PseudoClass.getPseudoClass("info");

    @FXML
    private Label filtersLabel;

    @FXML
    private TableView<HistoricPduView> table;
    @FXML
    private TableColumn<HistoricPduView, String> idColumn;
    @FXML
    private TableColumn<HistoricPduView, String> proxyColumn;
    @FXML
    private TableColumn<HistoricPduView, String> connectionColumn;
    @FXML
    private TableColumn<HistoricPduView, PduDestination> destinationColumn;
    @FXML
    private TableColumn<HistoricPduView, String> interceptorColumn;
    @FXML
    private TableColumn<HistoricPduView, String> tagsColumn;
    @FXML
    private TableColumn<HistoricPduView, Integer> sizeColumn;
    @FXML
    private TableColumn<HistoricPduView, Instant> timeColumn;

    @FXML
    private TextField proxyField;
    @FXML
    private TextField connectionField;
    @FXML
    private TextField destinationField;
    @FXML
    private TextField interceptorField;
    @FXML
    private TextField sizeField;
    @FXML
    private TextField timeField;
    @FXML
    private TextField tagsField;
    @FXML
    private BytesEditor bytesEditor;
    @FXML
    private TextArea metadataArea;
    @FXML
    private AnchorPane metadataPane;

    private final HistoryView view;
    private final HistoryApi api;
    private final ExtensionHelper extensionHelper;

    /**
     * Initializes table and refreshes view, so that the PDUs are loaded.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        metadataPane.managedProperty().bind(metadataPane.visibleProperty());
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        proxyColumn.setCellValueFactory(new PropertyValueFactory<>("proxyName"));
        connectionColumn.setCellValueFactory(new PropertyValueFactory<>("connectionName"));
        destinationColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        interceptorColumn.setCellValueFactory(new PropertyValueFactory<>("interceptorName"));
        tagsColumn.setCellValueFactory(new PropertyValueFactory<>("tags"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        timeColumn.setCellFactory(cell -> new InstantCellFactory<>());
        timeColumn.setCellFactory(cell -> new InstantCellFactory<>());
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.getSelectionModel().selectedItemProperty().addListener(this::onHistoryItemSelect);
        // Refresh items
        view.setFilter(view.getFilter());
        initContextMenu();
        metadataPane.setVisible(false);
    }

    /**
     * Handles change of filter (updates text in filterLabel).
     */
    public void onFilterChange() {
        var joiner = new StringJoiner("; ");
        if (view.getFilter().getProxyId() != null) {
            joiner.add("proxy");
        }

        if (view.getFilter().getInterceptorId() != null) {
            joiner.add("interceptor");
        }

        if (view.getFilter().getConnectionId() != null) {
            joiner.add("connection");
        }

        if (view.getFilter().getDestination() != null) {
            joiner.add("destination (" + view.getFilter().getDestination().name() + ")");
        }

        if (view.getFilter().getFromSize() != null) {
            joiner.add("from size (" + view.getFilter().getFromSize().toString() + ")");
        }

        if (view.getFilter().getToSize() != null) {
            joiner.add("to size (" + view.getFilter().getToSize().toString() + ")");
        }

        if (view.getFilter().getTags() != null && !view.getFilter().getTags().isEmpty()) {
            var tagJoiner = new StringJoiner(", ");
            view.getFilter().getTags().forEach(tagJoiner::add);
            joiner.add("tags (" + tagJoiner.toString() + ")");
        }

        if (view.getFilter().getData() != null) {
            joiner.add("data");
        }

        if (joiner.length() == 0) {
            filtersLabel.setText("-");
            return;
        }

        filtersLabel.setText(joiner.toString());
    }

    /**
     * Sets historic PDU views to the table.
     * @param items Items to be set to the table
     */
    public void setItems(List<HistoricPduView> items) {
        Platform.runLater(() -> {
            clearViewer();
            table.setItems(FXCollections.observableList(items));
            table.scrollTo(items.size() - 1);
        });
    }

    /**
     * Removes historic PDU views with specified identifiers from the table.
     * @param ids Identifiers of PDU views to remove
     */
    public void removeItems(Collection<Long> ids) {
        Platform.runLater(() -> {
            var deletedIds = new LinkedList<>(ids);
            if (deletedIds.size() > 100) {
                var newItems = this.table.getItems().stream()
                        .filter(pdu -> !deletedIds.remove(pdu.getId()))
                        .collect(Collectors.toCollection(LinkedList::new));
                table.setItems(FXCollections.observableList(newItems));
                return;
            }

            if (table.getItems() != null) {
                table.getItems().removeIf(pdu -> deletedIds.remove(pdu.getId()));
            }
        });
    }

    /**
     * Adds historic PDU view to the table
     * @param item Historic PDU view to be added to the table
     */
    public void addItem(HistoricPduView item) {
        Platform.runLater(() -> {
            if (this.table.getItems() != null) {
                this.table.getItems().add(item);
            }
        });
    }

    /**
     * Initializes context menu of table and adds "send to", "delete" and "clear" menu items to it.
     */
    private void initContextMenu() {
        var receivers = extensionHelper.getReceivers(SerializedPdu.class).stream()
                .map(receiver -> createSendToMenuItem(receiver.getCode(), receiver.getName()))
                .collect(Collectors.toList());

        var separator = new SeparatorMenuItem();

        var deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(this::onDeleteItemClick);

        var deleteAllItem = new MenuItem("Clear history");
        deleteAllItem.setOnAction(this::onDeleteAllItemClick);

        var menu = new ContextMenu();
        if (!receivers.isEmpty()) {
            menu.getItems().addAll(receivers);
            menu.getItems().add(separator);
        }
        menu.getItems().addAll(deleteItem, deleteAllItem);
        table.setContextMenu(menu);
    }

    /**
     * Creates menu item "Send to" for PETEP receivers.
     */
    private MenuItem createSendToMenuItem(String code, String name) {
        var item = new MenuItem("Send to: " + name);
        item.setUserData(code);
        item.setOnAction(event -> onSendItemClick(item));
        return item;
    }

    /**
     * Sends selected serialized PDU to receiver.
     */
    private void onSendItemClick(MenuItem item) {
        var pduView = table.getSelectionModel().getSelectedItem();
        if (pduView == null) {
            return;
        }

        var futurePdu = api.getService().getPdu(pduView.getId());
        futurePdu.thenAccept(maybeHistoricPdu -> {
            if (maybeHistoricPdu.isEmpty()) {
                return;
            }

            var receiver = (String) item.getUserData();
            var historicPdu = maybeHistoricPdu.get();
            extensionHelper.sendToReceiver(
                    receiver,
                    SerializedPdu.builder()
                            .proxy(historicPdu.getProxy().getCode())
                            .connection(historicPdu.getConnection().getCode())
                            .interceptor(historicPdu.getInterceptor().getCode())
                            .destination(historicPdu.getDestination())
                            .buffer(historicPdu.getData())
                            .charset(historicPdu.getCharset())
                            .tags(historicPdu.getTags())
                            .metadata(historicPdu.getMetadata())
                            .build()
            );
        });
    }

    /**
     * Deletes selected history PDUs.
     */
    private void onDeleteItemClick(ActionEvent event) {
        var selectedPdus = table.getSelectionModel().getSelectedItems();
        if (selectedPdus == null) {
            return;
        }
        api.getService().deletePdus(selectedPdus.stream().map(HistoricPduView::getId).collect(Collectors.toList()));
    }

    /**
     * Deletes all history PDUs.
     */
    private void onDeleteAllItemClick(ActionEvent event) {
        var confirmed = Dialogs.createYesOrNoDialog("Clear history confirmation", "Do you want to clear the whole history?");
        if (!confirmed) {
            return;
        }
        api.getService().clearHistory();
    }

    /**
     * Changes historic PDU view to display information about the selected historic PDU.
     */
    private void onHistoryItemSelect(ObservableValue<? extends HistoricPduView> observable, HistoricPduView oldItem, HistoricPduView newItem) {
        if (newItem == null) {
            clearViewer();
            return;
        }

        var futurePdu = api.getService().getPdu(newItem.getId());

        proxyField.setText(newItem.getProxyName());
        connectionField.setText(newItem.getConnectionName());
        destinationField.setText(newItem.getDestination().name());
        interceptorField.setText(newItem.getInterceptorName());
        sizeField.setText(String.valueOf(newItem.getSize()));
        timeField.setText(Constant.DATE_TIME_FORMATTER.format(newItem.getTime().atZone(ZoneId.systemDefault())));
        var tagJoiner = new StringJoiner(", ");
        newItem.getTags().forEach(tagJoiner::add);
        tagsField.setText(tagJoiner.toString());
        bytesEditor.setDisable(true);
        metadataArea.setDisable(true);
        bytesEditor.clear();
        metadataArea.clear();

        // Let additional info be updated when the PDU is obtained from database
        futurePdu.thenAccept(optionalPdu -> Platform.runLater(() -> {
            if (optionalPdu.isEmpty()) {
                return;
            }
            var pdu = optionalPdu.get();
            bytesEditor.setData(pdu.getData(), pdu.getCharset());
            bytesEditor.setDisable(false);

            if (pdu.getMetadata() == null || pdu.getMetadata().isEmpty()) {
                metadataPane.setVisible(false);
                return;
            }

            metadataPane.setVisible(true);
            metadataArea.setText(GuiUtils.formatMetadata(pdu.getMetadata()));
            metadataArea.setDisable(false);
        }));
    }

    /**
     * Displays History Filter Dialog for modifying filter.
     */
    @FXML
    private void onFilterClick(ActionEvent event) {
        try {
            var dialog = new HistoryFilterDialog(view.getFilter(), api.getService());
            var data = dialog.showAndWait();

            if (data.isEmpty()) {
                return;
            }

            view.setFilter(data.get());
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Exception during openning of history filter dialog", e);
        }
    }

    /**
     * Clears information about PDU.
     */
    private void clearViewer() {
        proxyField.clear();
        connectionField.clear();
        destinationField.clear();
        interceptorField.clear();
        sizeField.clear();
        timeField.clear();
        tagsField.clear();
        bytesEditor.clear();
        metadataArea.clear();
    }
}
