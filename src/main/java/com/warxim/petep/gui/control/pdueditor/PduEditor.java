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
package com.warxim.petep.gui.control.pdueditor;

import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.listener.ConnectionListener;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.gui.component.PduMetadataPane;
import com.warxim.petep.gui.control.byteseditor.BytesEditor;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.worker.Interceptor;
import com.warxim.petep.proxy.worker.Proxy;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * PDU editor.
 * <p>
 *     Uses byte editor for editing PDU byte buffer and optionally metadata editor for editing metadata of PDUs.
 * </p>
 * <p>
 *     For correct usage, PDU editor has to be initialized using PetepHelper,
 *     so that it can generate PDUs using active PETEP core.
 * </p>
 * <p>
 *     After PETEP stops, PDU editor should be destroyed, so that the resources are released.
 *     This is done automatically by default, but you can manage it yourself by setting {@link PduEditorConfig#isAutomaticLifecycle()} to false.</p>
 * </p>
 */
@PetepAPI
public class PduEditor extends AnchorPane implements ConnectionListener {
    private static final Interceptor NULL_INTERCEPTOR = new NullInterceptor();

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
    private ScrollPane metadataScrollPane;

    /**
     * List of interceptors in direction C2S (Client -&gt; Server)
     */
    private ObservableList<Interceptor> interceptorsC2S;
    /**
     * List of interceptors in direction S2C (Server -&gt; Client)
     */
    private ObservableList<Interceptor> interceptorsS2C;
    /**
     * List of proxies
     */
    private List<Proxy> proxies;
    /**
     * Extension helper for working with basic functionality
     */
    private ExtensionHelper extensionHelper;
    /**
     * Configuration of the editor
     */
    private PduEditorConfig config;
    /**
     * Lifecycle automator for automation of loading/unloading of editor
     */
    private PduEditorLifecycleAutomator lifecycleAutomator;

    /**
     * Constructs PDU editor.
     * <p>For full use, you have to initialize it using init method, and after PETEP core stops, destroy it.</p>
     * @throws IOException If the template could not be loaded
     */
    public PduEditor() throws IOException {
        var loader = new FXMLLoader(getClass().getResource("/fxml/control/PduEditor.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.setClassLoader(getClass().getClassLoader());
        loader.load();

        destinationInput.setItems(FXCollections.observableArrayList(PduDestination.CLIENT, PduDestination.SERVER));

        connectionInput.focusedProperty().addListener(this::onConnectionFocusChange);
        proxyInput.valueProperty().addListener(this::onProxyChange);
        destinationInput.valueProperty().addListener(this::onDestinationChange);

        metadataScrollPane.managedProperty().bind(metadataScrollPane.visibleProperty());
    }

    /**
     * Initializes the PDU editor.
     * <p>Can be called only once!</p>
     * <p><b>Note:</b> do not forget to call {@link #init} and {@link #unload} when {@link PduEditorConfig#isAutomaticLifecycle()} is false.</p>
     * @param extensionHelper Extension helper
     * @param config Configuration describing how the editor should work
     */
    public void init(ExtensionHelper extensionHelper, PduEditorConfig config) {
        if (this.extensionHelper != null) {
            throw new IllegalStateException("Cannot initialize PduEditor twice!");
        }
        this.config = config;
        this.extensionHelper = extensionHelper;
        if (config.isAutomaticLifecycle()) {
            // Start life-cycle automator
            lifecycleAutomator = new PduEditorLifecycleAutomator(this);
            lifecycleAutomator.start(extensionHelper);
        }
    }

    /**
     * Initializes the PDU editor.
     * <p>Use this method if {@link PduEditorConfig#isAutomaticLifecycle()} is false.</p>
     * <p><b>Note:</b> Do not forget to destroy the editor after PETEP core stops.</p>
     * @param helper PETEP helper for initializing the editor
     */
    public void load(PetepHelper helper) {
        proxies = helper.getProxies();
        proxyInput.setItems(FXCollections.observableList(proxies));

        interceptorsC2S = FXCollections.observableArrayList(helper.getInterceptorsC2S());
        interceptorsC2S.add(NULL_INTERCEPTOR);

        interceptorsS2C = FXCollections.observableArrayList(helper.getInterceptorsS2C());
        interceptorsS2C.add(NULL_INTERCEPTOR);

        helper.registerConnectionListener(this);
    }

    /**
     * Checks whether the PDU editor is loaded.
     * @return {@code true} if the editor is loaded
     */
    public boolean isLoaded() {
        return proxies != null;
    }

    /**
     * Destroys the PDU editor.
     * <p>Use this method if {@link PduEditorConfig#isAutomaticLifecycle()} is false.</p>
     */
    public void unload() {
        clear();
        proxies = null;
        interceptorsC2S = null;
        interceptorsS2C = null;
    }

    /**
     * Obtains PDU from the editor.
     * @return Created PDU
     */
    public Optional<PDU> getPdu() {
        try {
            PDU pdu;

            var pane = getMetadataPane();
            var proxy = proxyInput.getValue();
            var connection = connectionInput.getValue();
            var destination = destinationInput.getValue();
            var buffer = dataInput.getBytes();
            var charset = dataInput.getCharset();
            var tags = new HashSet<>(tagsList.getItems());
            if (pane.isPresent()) {
                pdu = pane.get().createPdu(
                        proxy,
                        connection,
                        destination,
                        buffer,
                        buffer.length,
                        charset,
                        tags).orElseThrow();
            } else {
                var deserializer = proxy.getModule().getFactory().getDeserializer();
                pdu = deserializer.deserializePdu(
                        proxy,
                        connection,
                        destination,
                        buffer,
                        buffer.length,
                        charset,
                        tags,
                        Map.of()
                ).orElseThrow();
            }

            var interceptorId = interceptorInput.getSelectionModel().getSelectedIndex();
            if (interceptorId != 0) {
                pdu.setLastInterceptor(interceptorInput.getItems().get(interceptorId - 1));
            }

            return Optional.of(pdu);
        } catch (RuntimeException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not obtain PDU from editor!", e);
            return Optional.empty();
        }
    }

    /**
     * Obtains PDU from the editor if valid.
     * @return  PDU if valid;
     *         {@code Optional.empty()} in case that the PDU could not be obtained (inactive core, invalid PDU, ...)
     */
    public Optional<PDU> validateAndGetPdu() {
        if (isValid()) {
            return getPdu();
        }
        return Optional.empty();
    }

    /**
     * Sets PDU to the editor.
     * @param pdu PDU to be set
     */
    public void setPdu(PDU pdu) {
        if (config.isStrict()) {
            proxyInput.setItems(
                    proxies.stream()
                            .filter(proxy -> proxy.supports(pdu))
                            .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        }

        dataInput.setData(pdu.getBuffer(), pdu.getSize(), pdu.getCharset());
        tagsList.setItems(FXCollections.observableArrayList(pdu.getTags()));

        var proxy = pdu.getProxy();
        proxyInput.getSelectionModel().select(proxy);

        var pane = getMetadataPane();
        if (pane.isPresent()) {
            pane.get().setPdu(pdu);
        }

        destinationInput.getSelectionModel().select(pdu.getDestination());

        if (config.isStrict()) {
            connectionInput.setItems(
                    proxy.getConnectionManager().getList().stream()
                            .filter(connection -> connection.supports(pdu))
                            .collect(Collectors.toCollection(FXCollections::observableArrayList)));
        } else {
            connectionInput.setItems(FXCollections.observableList(proxy.getConnectionManager().getList()));
        }
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

    /**
     * Clears the PDU editor.
     */
    public void clear() {
        dataInput.setBytes(new byte[0]);
        tagsList.getItems().clear();
        proxyInput.getSelectionModel().clearSelection();
        destinationInput.getSelectionModel().clearSelection();
        connectionInput.getSelectionModel().clearSelection();

        var pane = getMetadataPane();
        if (pane.isPresent()) {
            pane.get().clear();
        }
    }

    /**
     * Checks whether the data in the editor are valid.
     * @return {@code true} if the data are valid
     */
    public boolean isValid() {
        if (connectionInput.getSelectionModel().getSelectedItem() == null) {
            Dialogs.createErrorDialog("Connection required", "You have to select connection.");
            return false;
        }

        if (proxyInput.getSelectionModel().getSelectedItem() == null) {
            Dialogs.createErrorDialog("Proxy required", "You have to select proxy.");
            return false;
        }

        if (destinationInput.getSelectionModel().getSelectedItem() == null) {
            Dialogs.createErrorDialog("Destination required", "You have to select destination.");
            return false;
        }

        if (interceptorInput.getSelectionModel().getSelectedItem() == null) {
            Dialogs.createErrorDialog("Interceptor required", "You have to select target interceptor.");
            return false;
        }

        var pane = getMetadataPane();
        if (pane.isEmpty()) {
            return true;
        }

        return pane.get().isValid();
    }

    /**
     * Removes connection from connection input when it stops.
     */
    @Override
    public void onConnectionStop(Connection connection) {
        Platform.runLater(() -> {
            var proxy = proxyInput.getSelectionModel().getSelectedItem();
            if (!connection.getProxy().equals(proxy)) {
                return;
            }

            var selectedConnection = connectionInput.getSelectionModel().getSelectedItem();
            if (selectedConnection != null && selectedConnection.equals(connection)) {
                connectionInput.getSelectionModel().clearSelection();
            }
        });
    }

    /**
     * Shows dialog for adding tag to the PDU.
     */
    @FXML
    protected void onAddTagButtonClick(ActionEvent event) {
        var tag = Dialogs.createTextInputDialog("New tag", "New tag: ");
        if (tag.isPresent()) {
            tagsList.getItems().add(tag.get());
        }
    }

    /**
     * Removes selected tag from the PDU.
     */
    @FXML
    protected void onRemoveTagButtonClick(ActionEvent event) {
        int index = tagsList.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            return;
        }

        tagsList.getItems().remove(tagsList.getSelectionModel().getSelectedIndex());
    }

    /**
     * Returns metadata pane if it exists.
     */
    private Optional<PduMetadataPane> getMetadataPane() {
        if (!metadataPane.getChildren().isEmpty()) {
            return Optional.of((PduMetadataPane) metadataPane.getChildren().get(0));
        }

        return Optional.empty();
    }

    /**
     * Loads metadata pane for given proxy.
     */
    private void loadMetadataPane(Proxy proxy) {
        metadataPane.getChildren().clear();

        try {
            var maybePane = proxy.getModule().getFactory().createPduMetadataPane();
            if (maybePane.isPresent()) {
                var pane = maybePane.get();
                AnchorPane.setLeftAnchor(pane, 0D);
                AnchorPane.setRightAnchor(pane, 0D);
                metadataPane.getChildren().add(pane);
                metadataScrollPane.setVisible(true);
            } else {
                metadataScrollPane.setVisible(false);
            }
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Exception during PDU metadata pane creation.", e);

            proxyInput.getSelectionModel().clearSelection();
        }
    }

    /**
     * Loads selected proxy interface.
     */
    private void onProxyChange(ObservableValue<? extends Proxy> observable, Proxy oldValue, Proxy newValue) {
        if (newValue == null) {
            return;
        }

        connectionInput.getSelectionModel().clearSelection();

        if (oldValue == null || oldValue.getModule().getFactory() != newValue.getModule().getFactory()) {
            loadMetadataPane(newValue);
        }
    }

    /**
     * Reloads connection list when connection box gets focused.
     */
    private void onConnectionFocusChange(ObservableValue<? extends Boolean> observable, boolean oldValue, boolean newValue) {
        if (!newValue) {
            return;
        }

        var proxy = proxyInput.getSelectionModel().getSelectedItem();
        if (proxy == null) {
            return;
        }

        connectionInput.setItems(FXCollections.observableList(proxy.getConnectionManager().getList()));
    }

    /**
     * Changes interceptor list on destination change.
     */
    private void onDestinationChange(ObservableValue<? extends PduDestination> observable, PduDestination oldValue, PduDestination newValue) {
        if (newValue == PduDestination.CLIENT) {
            interceptorInput.setItems(interceptorsS2C);
        } else {
            interceptorInput.setItems(interceptorsC2S);
        }
    }

}
