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

import java.io.IOException;
import java.util.HashMap;
import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.http.pdu.HttpPdu;
import com.warxim.petep.extension.internal.http.pdu.HttpRequestPdu;
import com.warxim.petep.extension.internal.http.pdu.HttpResponsePdu;
import com.warxim.petep.extension.internal.http.pdu.Opcode;
import com.warxim.petep.extension.internal.http.pdu.WebSocketPdu;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

public final class HttpMetadataPane extends PduMetadataPane {
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

  private final ObservableMap<String, String> httpHeaderMap;

  private final ObservableList<String> httpHeaderNameList;

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
    websocketOpcodeInput.setItems(FXCollections.observableArrayList(Opcode.TEXT, Opcode.BINARY,
        Opcode.CLOSE, Opcode.PING, Opcode.PONG, Opcode.CONTINUATION, Opcode.NON_CONTROL_1,
        Opcode.NON_CONTROL_2, Opcode.NON_CONTROL_3, Opcode.NON_CONTROL_4, Opcode.NON_CONTROL_5,
        Opcode.CONTROL_1, Opcode.CONTROL_2, Opcode.CONTROL_3, Opcode.CONTROL_4, Opcode.CONTROL_5));
  }

  private void onWebsocketMaskedInputChange(
      ObservableValue<? extends Boolean> observable,
      boolean oldValue,
      boolean newValue) {
    websocketMaskInput.setDisable(oldValue);
  }

  private void onHeaderMapChange(
      MapChangeListener.Change<? extends String, ? extends String> change) {
    boolean removed = change.wasRemoved();
    if (removed != change.wasAdded()) {
      if (removed) {
        httpHeaderNameList.remove(change.getKey());
      } else {
        httpHeaderNameList.add(change.getKey());
      }
    }
  }

  @Override
  public PDU getPdu(
      Proxy proxy,
      Connection connection,
      PduDestination destination,
      byte[] buffer,
      int size) {
    if (httpPane.isVisible()) {
      return getHttpPdu(proxy, connection, destination, buffer, size);
    } else {
      return getWebSocketPdu(proxy, connection, destination, buffer, size);
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
        websocketMaskInput.setText(BytesUtils.bytesToString(((WebSocketPdu) pdu).getMask()));
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

  private HttpPdu getHttpPdu(
      Proxy proxy,
      Connection connection,
      PduDestination destination,
      byte[] buffer,
      int size) {
    // HTTP
    HttpPdu pdu;
    if (httpRequestPane.isVisible()) {
      // HTTP Request
      pdu = new HttpRequestPdu(proxy, connection, destination, buffer, size);

      ((HttpRequestPdu) pdu).setPath(httpPathInput.getText());
      ((HttpRequestPdu) pdu).setMethod(httpMethodInput.getText());
    } else {
      // HTTP Response
      pdu = new HttpResponsePdu(proxy, connection, destination, buffer, size);

      ((HttpResponsePdu) pdu).setStatusCode(Integer.parseInt(httpStatusCodeInput.getText()));
      ((HttpResponsePdu) pdu).setStatusMessage(httpStatusMessageInput.getText());
    }

    pdu.setVersion(httpVersionInput.getText());

    pdu.addHeaders(new HashMap<>(httpHeaderMap));

    return pdu;
  }

  private WebSocketPdu getWebSocketPdu(
      Proxy proxy,
      Connection connection,
      PduDestination destination,
      byte[] buffer,
      int size) {
    // WebSocket
    WebSocketPdu pdu = new WebSocketPdu(proxy, connection, destination, buffer, size);

    pdu.setFinal(websocketFinInput.isSelected());

    pdu.setRsv1(websocketRsv1Input.isSelected());
    pdu.setRsv2(websocketRsv2Input.isSelected());
    pdu.setRsv3(websocketRsv3Input.isSelected());

    pdu.setMasked(websocketMaskedInput.isSelected());

    pdu.setOpcode(websocketOpcodeInput.getValue());

    if (pdu.isMasked()) {
      pdu.setMask(BytesUtils.stringToBytes(websocketMaskInput.getText()));
    }

    return pdu;
  }

  @FXML
  private void onHttpHeaderAddButtonClick(ActionEvent event) {
    Pair<String, String> header = Dialogs.createTextPairDialog("Create header", "Name:", "Value:");

    if (header == null) {
      return;
    }

    httpHeaderMap.put(header.getKey(), header.getValue());
  }

  @FXML
  private void onHttpHeaderEditButtonClick(ActionEvent event) {
    String item = httpHeadersInput.getSelectionModel().getSelectedItem();

    if (item == null) {
      return;
    }

    Pair<String, String> header = Dialogs.createTextPairDialog("Create header", "Name:", "Value:",
        item, httpHeaderMap.get(item));

    if (header == null) {
      return;
    }

    if (header.getKey().equals(item)) {
      httpHeaderMap.replace(item, header.getValue());
    } else {
      httpHeaderMap.remove(item);
      httpHeaderMap.put(header.getKey(), header.getValue());
    }
  }

  @FXML
  private void onHttpHeaderRemoveButtonClick(ActionEvent event) {
    String item = httpHeadersInput.getSelectionModel().getSelectedItem();

    if (item == null) {
      return;
    }

    httpHeaderMap.remove(item);
  }
}
