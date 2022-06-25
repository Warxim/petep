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
package com.warxim.petep.extension.internal.http.gui;

import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.http.pdu.*;
import com.warxim.petep.gui.component.PduMetadataPane;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.proxy.worker.Proxy;
import com.warxim.petep.util.BytesUtils;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

/**
 * HTTP metadata pane
 */
public final class HttpMetadataPane extends PduMetadataPane {
    private final ObservableMap<String, String> httpHeaderMap;
    private final ObservableList<String> httpHeaderNameList;

    /*
     * HTTP
     */
    @FXML
    private AnchorPane httpPane;
    @FXML
    private TextField httpVersionInput;
    @FXML
    private TableView<String> httpHeadersInput;
    @FXML
    private TableColumn<String, String> httpHeaderNameColumn;
    @FXML
    private TableColumn<String, String> httpHeaderValueColumn;
    @FXML
    private AnchorPane httpRequestPane;
    @FXML
    private TextField httpMethodInput;
    @FXML
    private TextField httpPathInput;
    @FXML
    private AnchorPane httpResponsePane;
    @FXML
    private TextField httpStatusCodeInput;
    @FXML
    private TextField httpStatusMessageInput;
    /*
     * WEBSOCKET
     */
    @FXML
    private AnchorPane websocketPane;
    @FXML
    private CheckBox websocketFinInput;
    @FXML
    private CheckBox websocketRsv1Input;
    @FXML
    private CheckBox websocketRsv2Input;
    @FXML
    private CheckBox websocketRsv3Input;
    @FXML
    private CheckBox websocketMaskedInput;
    @FXML
    private ComboBox<Opcode> websocketOpcodeInput;
    @FXML
    private TextField websocketMaskInput;

    /**
     * Constructs metadata pane for HTTP PDUs.
     * @throws IOException If the template could not be loaded
     */
    public HttpMetadataPane() throws IOException {
        super("/fxml/extension/internal/http/HttpPduMetadata.fxml");

        httpHeaderMap = FXCollections.observableHashMap();
        httpHeaderNameList = FXCollections.observableArrayList();

        httpHeaderMap.addListener(this::onHeaderMapChange);

        httpHeaderNameColumn.setCellValueFactory(cd -> Bindings.createStringBinding(cd::getValue));
        httpHeaderValueColumn.setCellValueFactory(cd -> Bindings.valueAt(httpHeaderMap, cd.getValue()));

        httpHeadersInput.setItems(httpHeaderNameList);

        httpPane.managedProperty().bind(httpPane.visibleProperty());
        httpResponsePane.managedProperty().bind(httpResponsePane.visibleProperty());
        httpRequestPane.managedProperty().bind(httpRequestPane.visibleProperty());
        websocketPane.managedProperty().bind(websocketPane.visibleProperty());

        // Disable mask input automatically.
        websocketMaskedInput.selectedProperty().addListener(this::onWebsocketMaskedInputChange);

        // Setup opcodes.
        websocketOpcodeInput.setItems(FXCollections.observableArrayList(
                Opcode.TEXT, Opcode.BINARY, Opcode.CLOSE, Opcode.PING, Opcode.PONG,  Opcode.CONTINUATION,
                Opcode.NON_CONTROL_1, Opcode.NON_CONTROL_2, Opcode.NON_CONTROL_3, Opcode.NON_CONTROL_4,  Opcode.NON_CONTROL_5,
                Opcode.CONTROL_1, Opcode.CONTROL_2, Opcode.CONTROL_3, Opcode.CONTROL_4, Opcode.CONTROL_5
        ));
    }

    @Override
    public Optional<PDU> createPdu(
            Proxy proxy,
            Connection connection,
            PduDestination destination,
            byte[] buffer,
            int size,
            Charset charset,
            Set<String> tags) {
        if (httpPane.isVisible()) {
            if (httpRequestPane.isVisible()) {
                var pdu = new HttpRequestPdu(proxy, connection, destination, buffer, size, charset, tags);
                updateHttpPdu(pdu);
                return Optional.of(pdu);
            } else {
                var pdu = new HttpResponsePdu(proxy, connection, destination, buffer, size, charset, tags);
                updateHttpPdu(pdu);
                return Optional.of(pdu);
            }
        } else {
            var pdu = new WebSocketPdu(proxy, connection, destination, buffer, size, charset, tags);
            updateWebSocketPdu(pdu);
            return Optional.of(pdu);
        }
    }

    @Override
    public void setPdu(PDU pdu) {
        if (pdu instanceof HttpPdu) {
            // HTTP
            httpPane.setVisible(true);
            websocketPane.setVisible(false);

            httpHeaderMap.putAll(((HttpPdu) pdu).getHeaders());

            httpVersionInput.setText(((HttpPdu) pdu).getVersion());

            if (pdu instanceof HttpRequestPdu) {
                // HTTP Request
                httpRequestPane.setVisible(true);
                httpResponsePane.setVisible(false);
                httpMethodInput.setText(((HttpRequestPdu) pdu).getMethod());
                httpPathInput.setText(((HttpRequestPdu) pdu).getPath());
            } else {
                // HTTP Response
                httpRequestPane.setVisible(false);
                httpResponsePane.setVisible(true);
                httpStatusCodeInput.setText(String.valueOf(((HttpResponsePdu) pdu).getStatusCode()));
                httpStatusMessageInput.setText(((HttpResponsePdu) pdu).getStatusMessage());
            }
        } else {
            // WebSocket
            httpPane.setVisible(false);
            httpRequestPane.setVisible(false);
            httpResponsePane.setVisible(false);
            websocketPane.setVisible(true);

            websocketFinInput.setSelected(((WebSocketPdu) pdu).isFinal());
            websocketRsv1Input.setSelected(((WebSocketPdu) pdu).isRsv1());
            websocketRsv2Input.setSelected(((WebSocketPdu) pdu).isRsv2());
            websocketRsv3Input.setSelected(((WebSocketPdu) pdu).isRsv3());
            websocketMaskedInput.setSelected(((WebSocketPdu) pdu).isMasked());

            websocketOpcodeInput.setValue(((WebSocketPdu) pdu).getOpcode());

            if (((WebSocketPdu) pdu).isMasked()) {
                websocketMaskInput.setText(BytesUtils.bytesToHexString(((WebSocketPdu) pdu).getMask()));
            } else {
                websocketMaskInput.setText("");
            }
        }
    }

    @Override
    public void clear() {
        httpHeadersInput.getItems().clear();
        httpVersionInput.clear();
        httpMethodInput.clear();
        httpPathInput.clear();
        httpStatusCodeInput.clear();
        httpStatusMessageInput.clear();
        websocketOpcodeInput.getSelectionModel().clearSelection();
        websocketFinInput.setSelected(false);
        websocketRsv1Input.setSelected(false);
        websocketRsv2Input.setSelected(false);
        websocketRsv3Input.setSelected(false);
        websocketMaskedInput.setSelected(false);
        websocketMaskInput.clear();
    }

    @Override
    public boolean isValid() {
        return true;
    }

    /**
     * Lets user add new header through dialog.
     */
    @FXML
    private void onHttpHeaderAddButtonClick(ActionEvent event) {
        var maybeHeader = Dialogs.createTextPairDialog("Create header", "Name:", "Value:");

        if (maybeHeader.isEmpty()) {
            return;
        }
        var header = maybeHeader.get();

        httpHeaderMap.put(header.getKey(), header.getValue());
    }

    /**
     * Lets user edit selected header through dialog.
     */
    @FXML
    private void onHttpHeaderEditButtonClick(ActionEvent event) {
        var item = httpHeadersInput.getSelectionModel().getSelectedItem();

        if (item == null) {
            return;
        }

        var maybeHeader = Dialogs.createTextPairDialog(
                "Create header",
                "Name:",
                "Value:",
                item,
                httpHeaderMap.get(item)
        );

        if (maybeHeader.isEmpty()) {
            return;
        }
        var header = maybeHeader.get();

        if (header.getKey().equals(item)) {
            httpHeaderMap.replace(item, header.getValue());
        } else {
            httpHeaderMap.remove(item);
            httpHeaderMap.put(header.getKey(), header.getValue());
        }
    }

    /**
     * Removes selected header.
     */
    @FXML
    private void onHttpHeaderRemoveButtonClick(ActionEvent event) {
        var item = httpHeadersInput.getSelectionModel().getSelectedItem();

        if (item == null) {
            return;
        }

        httpHeaderMap.remove(item);
    }

    /**
     * Handles change of webSocket mask checkbox input. (Disables/enables mask input.)
     */
    private void onWebsocketMaskedInputChange(
            ObservableValue<? extends Boolean> observable,
            boolean oldValue,
            boolean newValue) {
        websocketMaskInput.setDisable(oldValue);
    }

    /**
     * Handles change of header map and updates http header name list.
     */
    private void onHeaderMapChange(MapChangeListener.Change<? extends String, ? extends String> change) {
        if (change.wasRemoved()) {
            httpHeaderNameList.remove(change.getKey());
        } else if (change.wasAdded()) {
            httpHeaderNameList.add(change.getKey());
        }
    }

    /**
     * Updates HTTP PDU using data from inputs.
     */
    private void updateHttpPdu(HttpPdu pdu) {
        // HTTP
        if (httpRequestPane.isVisible()) {
            // HTTP Request
            ((HttpRequestPdu) pdu).setPath(httpPathInput.getText());
            ((HttpRequestPdu) pdu).setMethod(httpMethodInput.getText());
        } else {
            // HTTP Response
            ((HttpResponsePdu) pdu).setStatusCode(Integer.parseInt(httpStatusCodeInput.getText()));
            ((HttpResponsePdu) pdu).setStatusMessage(httpStatusMessageInput.getText());
        }
        pdu.setVersion(httpVersionInput.getText());
        pdu.setHeaders(new HashMap<>(httpHeaderMap));
    }

    /**
     * Updates WebSocket PDU using data from inputs.
     */
    private void updateWebSocketPdu(WebSocketPdu pdu) {
        // WebSocket
        pdu.setFinal(websocketFinInput.isSelected());

        pdu.setRsv1(websocketRsv1Input.isSelected());
        pdu.setRsv2(websocketRsv2Input.isSelected());
        pdu.setRsv3(websocketRsv3Input.isSelected());

        pdu.setMasked(websocketMaskedInput.isSelected());

        pdu.setOpcode(websocketOpcodeInput.getValue());

        if (pdu.isMasked()) {
            pdu.setMask(BytesUtils.hexStringToBytes(websocketMaskInput.getText()));
        }
    }
}
