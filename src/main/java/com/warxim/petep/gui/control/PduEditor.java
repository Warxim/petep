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
package com.warxim.petep.gui.control;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.pdu.DefaultPdu;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.gui.component.PduMetadataPane;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.worker.Interceptor;
import com.warxim.petep.proxy.worker.Proxy;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

/** PDU editor. */
@PetepAPI
public class PduEditor extends AnchorPane {
  private static final Interceptor nullInterceptor = new Interceptor(0, null, null) {
    @Override
    public void stop() {
      // No action needed.
    }

    @Override
    public boolean prepare() {
      return false;
    }

    @Override
    public boolean intercept(PDU pdu) {
      return false;
    }

    @Override
    public String toString() {
      return "Send out of PETEP";
    }
  };

  @FXML
  private ListView<String> tagsList;

  @FXML
  private BytesEditor dataInput;

  @FXML
  private ComboBox<Proxy> proxyInput;
  @FXML
  private ComboBox<Connection> connectionInput;
  @FXML
  private ComboBox<PduDestination> destinationInput;
  @FXML
  private ComboBox<Interceptor> interceptorInput;

  @FXML
  private AnchorPane metadataPane;
  @FXML
  private CheckBox flowInput;

  private ObservableList<Interceptor> interceptorsC2S;
  private ObservableList<Interceptor> interceptorsS2C;

  public PduEditor() throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/control/PduEditor.fxml"));
    loader.setRoot(this);
    loader.setController(this);
    loader.setClassLoader(getClass().getClassLoader());
    loader.load();

    destinationInput.setItems(FXCollections.observableArrayList(PduDestination.CLIENT, PduDestination.SERVER));

    connectionInput.focusedProperty().addListener(this::onConnectionFocusChange);
    proxyInput.valueProperty().addListener(this::onProxyChange);
    destinationInput.valueProperty().addListener(this::onDestinationChange);
    flowInput.selectedProperty().addListener(this::onFlowChange);

    proxyInput.setDisable(true);
    destinationInput.setDisable(true);
    connectionInput.setDisable(true);
    interceptorInput.setDisable(true);
  }

  /** Loads selected proxy interface. */
  private void onProxyChange(ObservableValue<? extends Proxy> observable, Proxy oldValue, Proxy newValue) {
    if (newValue == null) {
      return;
    }

    connectionInput.getSelectionModel().clearSelection();

    if (oldValue == null || oldValue.getModule().getFactory() != newValue.getModule().getFactory()) {
      loadMetadataPane(newValue);
    }
  }

  /** Reloads connection list when connection box gets focused. */
  private void onConnectionFocusChange(
      ObservableValue<? extends Boolean> observable,
      boolean oldValue,
      boolean newValue) {
    if (!newValue) {
      return;
    }

    Proxy proxy = proxyInput.getSelectionModel().getSelectedItem();
    if (proxy == null) {
      return;
    }

    connectionInput.setItems(FXCollections.observableList(proxy.getConnectionManager().getList()));
  }

  /** Shows/hides flow editor. */
  private void onFlowChange(ObservableValue<? extends Boolean> observable, boolean oldValue, boolean newValue) {
    proxyInput.setDisable(oldValue);
    destinationInput.setDisable(oldValue);
    connectionInput.setDisable(oldValue);
    interceptorInput.setDisable(oldValue);
  }

  /** Changes interceptor list on destination change. */
  private void onDestinationChange(
      ObservableValue<? extends PduDestination> observable,
      PduDestination oldValue,
      PduDestination newValue) {
    if (newValue == PduDestination.CLIENT) {
      interceptorInput.setItems(interceptorsS2C);
    } else {
      interceptorInput.setItems(interceptorsC2S);
    }
  }

  public void init(PetepHelper helper) {
    proxyInput.setItems(FXCollections.observableList(helper.getProxies()));

    interceptorsC2S = FXCollections.observableArrayList(helper.getInterceptorsC2S());
    interceptorsC2S.add(nullInterceptor);

    interceptorsS2C = FXCollections.observableArrayList(helper.getInterceptorsS2C());
    interceptorsS2C.add(nullInterceptor);
  }

  @FXML
  private void onAddTagButtonClick(ActionEvent event) {
    String tag = Dialogs.createTextInputDialog("New tag", "New tag: ");

    if (tag != null) {
      tagsList.getItems().add(tag);
    }
  }

  @FXML
  private void onRemoveTagButtonClick(ActionEvent event) {
    int index = tagsList.getSelectionModel().getSelectedIndex();

    if (index == -1) {
      return;
    }

    tagsList.getItems().remove(tagsList.getSelectionModel().getSelectedIndex());
  }

  private void loadMetadataPane(Proxy proxy) {
    metadataPane.getChildren().clear();

    try {
      PduMetadataPane pane = proxy.getModule().getFactory().createPduMetadataPane();
      if (pane != null) {
        AnchorPane.setLeftAnchor(pane, 0D);
        AnchorPane.setRightAnchor(pane, 0D);

        metadataPane.getChildren().add(pane);
      }
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Exception during PDU metadata pane creation.", e);

      proxyInput.getSelectionModel().clearSelection();
    }
  }

  public PDU getPdu() {
    PDU pdu;

    if (!metadataPane.getChildren().isEmpty()) {
      PduMetadataPane pane = ((PduMetadataPane) metadataPane.getChildren().get(0));
      pdu = pane.getPdu(proxyInput.getValue(), connectionInput.getValue(), destinationInput.getValue(),
          dataInput.getBytes(), dataInput.getBytes().length);
    } else {
      pdu = new DefaultPdu(proxyInput.getValue(), connectionInput.getValue(), destinationInput.getValue(),
          dataInput.getBytes(), dataInput.getBytes().length);
    }

    pdu.setCharset(dataInput.getCharset());

    int interceptorId = interceptorInput.getSelectionModel().getSelectedIndex();
    if (interceptorId != 0) {
      pdu.setLastInterceptor(interceptorInput.getItems().get(interceptorId - 1));
    }

    return pdu;
  }

  public void setPdu(PDU pdu) {
    dataInput.setData(pdu.getBuffer(), pdu.getSize(), pdu.getCharset());
    tagsList.setItems(FXCollections.observableArrayList(pdu.getTags()));

    Proxy proxy = pdu.getProxy();

    proxyInput.getSelectionModel().select(proxy);

    if (!metadataPane.getChildren().isEmpty()) {
      PduMetadataPane pane = ((PduMetadataPane) metadataPane.getChildren().get(0));

      pane.setPdu(pdu);
    }

    destinationInput.getSelectionModel().select(pdu.getDestination());

    connectionInput.setItems(FXCollections.observableList(proxy.getConnectionManager().getList()));
    connectionInput.getSelectionModel().select(pdu.getConnection());

    if (pdu.getDestination() == PduDestination.CLIENT) {
      interceptorInput.setItems(interceptorsS2C);
    } else {
      interceptorInput.setItems(interceptorsC2S);
    }

    if (pdu.getLastInterceptor() == null) {
      interceptorInput.getSelectionModel().select(0);
    } else {
      interceptorInput.getSelectionModel().select(pdu.getLastInterceptor().getId() + 1);
    }
  }

  public void clear() {
    dataInput.setBytes(new byte[0]);
    tagsList.getItems().clear();
    proxyInput.getSelectionModel().clearSelection();
    destinationInput.getSelectionModel().clearSelection();
    connectionInput.getSelectionModel().clearSelection();

    if (!metadataPane.getChildren().isEmpty()) {
      PduMetadataPane pane = ((PduMetadataPane) metadataPane.getChildren().get(0));
      pane.clear();
    }
  }
}
