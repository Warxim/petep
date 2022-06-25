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
package com.warxim.petep.extension.internal.tcp;

import com.warxim.petep.common.Constant;
import com.warxim.petep.gui.component.ConfigPane;
import com.warxim.petep.gui.dialog.Dialogs;
import com.warxim.petep.util.FileUtils;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * TCP configurator GUI.
 */
public final class TcpConfigurator extends ConfigPane<TcpConfig> {
    /*
     * TCP
     */
    @FXML
    private TextField proxyIpInput;
    @FXML
    private TextField proxyPortInput;
    @FXML
    private TextField targetIpInput;
    @FXML
    private TextField targetPortInput;
    @FXML
    private TextField bufferSizeInput;
    @FXML
    private TextField charsetInput;
    @FXML
    private TextField connectionCloseDelayInput;

    /*
     * SSL
     */

    /*
     * SERVER SSL
     */
    @FXML
    private ToggleGroup serverSsl;
    @FXML
    private RadioButton serverNoSslRadio;
    @FXML
    private RadioButton serverSslRadio;
    @FXML
    private ComboBox<String> serverAlgorithmInput;
    @FXML
    private TextField serverKeyStoreInput;
    @FXML
    private TextField serverKeyStorePasswordInput;
    @FXML
    private TextField serverKeyPasswordInput;
    @FXML
    private ComboBox<String> serverKeyStoreTypeInput;

    /*
     * CLIENT SSL
     */
    @FXML
    private ToggleGroup clientSsl;
    @FXML
    private RadioButton clientNoSslRadio;
    @FXML
    private RadioButton clientSslRadio;
    @FXML
    private RadioButton clientSslWithCertificateRadio;
    @FXML
    private ComboBox<String> clientAlgorithmInput;
    @FXML
    private TextField clientKeyStoreInput;
    @FXML
    private TextField clientKeyStorePasswordInput;
    @FXML
    private TextField clientKeyPasswordInput;
    @FXML
    private ComboBox<String> clientKeyStoreTypeInput;

    /**
     * Constructs TCP configurator.
     * @throws IOException If the template could not be loaded
     */
    public TcpConfigurator() throws IOException {
        super("/fxml/extension/internal/tcp/TcpConfigurator.fxml");

        proxyIpInput.setText("127.0.0.1");
        proxyPortInput.setText("8888");
        bufferSizeInput.setText("4096");
        connectionCloseDelayInput.setText("500");
        charsetInput.setText(Constant.DEFAULT_CHARSET.name());

        // SSL
        var algorithms =
                FXCollections.observableList(Arrays.asList("SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"));
        var keyStoreTypes =
                FXCollections.observableList(Arrays.asList("PKCS12", "PKCS11", "JKS"));

        // SERVER SSL
        serverSsl.selectedToggleProperty().addListener(this::onServerSslEnabledChange);

        serverNoSslRadio.setSelected(true);

        serverAlgorithmInput.setItems(algorithms);
        serverAlgorithmInput.getSelectionModel().selectLast();

        serverKeyStoreTypeInput.setItems(keyStoreTypes);
        serverKeyStoreTypeInput.getSelectionModel().selectLast();

        serverKeyStoreInput.setText("conf/server.jks");

        // CLIENT SSL
        clientSsl.selectedToggleProperty().addListener(this::onClientSslEnabledChange);

        clientNoSslRadio.setSelected(true);

        clientAlgorithmInput.setItems(FXCollections.observableList(algorithms));
        clientAlgorithmInput.getSelectionModel().selectLast();

        clientKeyStoreTypeInput.setItems(FXCollections.observableList(keyStoreTypes));
        clientKeyStoreTypeInput.getSelectionModel().selectLast();

        clientKeyStoreInput.setText("conf/client.jks");
    }

    /**
     * Get TCP configuration from configurator.
     */
    @Override
    public TcpConfig getConfig() {
        // Server SSL config.
        SslConfig server;
        if (serverSslRadio.isSelected()) {
            var certificateConfig = new SslCertificateConfig(
                    serverKeyStoreInput.getText(),
                    serverKeyStoreTypeInput.getValue(),
                    serverKeyStorePasswordInput.getText(),
                    serverKeyPasswordInput.getText());
            server = new SslConfig(serverAlgorithmInput.getValue(), certificateConfig);
        } else {
            server = null;
        }

        // Client SSL config.
        SslConfig client;
        if (clientSslRadio.isSelected()) {
            client = new SslConfig(clientAlgorithmInput.getValue(), null);
        } else if (clientSslWithCertificateRadio.isSelected()) {
            var certificateConfig = new SslCertificateConfig(
                    clientKeyStoreInput.getText(),
                    clientKeyStoreTypeInput.getValue(),
                    clientKeyStorePasswordInput.getText(),
                    clientKeyPasswordInput.getText());
            client = new SslConfig(clientAlgorithmInput.getValue(), certificateConfig);
        } else {
            client = null;
        }

        return new TcpConfig(
                proxyIpInput.getText(),
                targetIpInput.getText(),
                Integer.parseInt(proxyPortInput.getText()),
                Integer.parseInt(targetPortInput.getText()),
                Integer.parseInt(bufferSizeInput.getText()),
                Charset.forName(charsetInput.getText()),
                Integer.parseInt(connectionCloseDelayInput.getText()),
                server,
                client);
    }

    @Override
    public void setConfig(TcpConfig config) {
        proxyIpInput.setText(config.getProxyIP());
        targetIpInput.setText(config.getTargetIP());
        proxyPortInput.setText(String.valueOf(config.getProxyPort()));
        targetPortInput.setText(String.valueOf(config.getTargetPort()));
        bufferSizeInput.setText(String.valueOf(config.getBufferSize()));
        charsetInput.setText(config.getCharset().name());
        connectionCloseDelayInput.setText(String.valueOf(config.getConnectionCloseDelay()));

        // Server SSL config.
        var serverSslConfig = config.getServerSslConfig();
        if (serverSslConfig != null) {
            serverSslRadio.setSelected(true);

            serverAlgorithmInput.getSelectionModel().select(serverSslConfig.getAlgorithm());

            var certificateConfig = serverSslConfig.getCertificateConfig();

            serverKeyStoreInput.setText(certificateConfig.getKeyStore());
            serverKeyStoreTypeInput.setValue(certificateConfig.getKeyStoreType());
            serverKeyStorePasswordInput.setText(certificateConfig.getKeyStorePassword());
            serverKeyPasswordInput.setText(certificateConfig.getKeyPassword());
        }

        // Client SSL config.
        var clientSslConfig = config.getClientSslConfig();
        if (clientSslConfig != null) {
            clientAlgorithmInput.getSelectionModel().select(clientSslConfig.getAlgorithm());

            var certificateConfig = clientSslConfig.getCertificateConfig();

            if (certificateConfig != null) {
                clientSslWithCertificateRadio.setSelected(true);
                clientKeyStoreInput.setText(certificateConfig.getKeyStore());
                clientKeyStoreTypeInput.setValue(certificateConfig.getKeyStoreType());
                clientKeyStorePasswordInput.setText(certificateConfig.getKeyStorePassword());
                clientKeyPasswordInput.setText(certificateConfig.getKeyPassword());
            } else {
                clientSslRadio.setSelected(true);
            }
        }
    }

    @Override
    public boolean isValid() {
        if (proxyIpInput.getText().length() == 0) {
            Dialogs.createErrorDialog("Proxy IP required", "You have to enter proxy IP.");
            return false;
        }
        if (proxyPortInput.getText().length() == 0) {
            Dialogs.createErrorDialog("Proxy port required", "You have to enter proxy port.");
            return false;
        }

        if (targetIpInput.getText().length() == 0) {
            Dialogs.createErrorDialog("Target IP required", "You have to enter target IP.");
            return false;
        }

        if (targetPortInput.getText().length() == 0) {
            Dialogs.createErrorDialog("Target port required", "You have to enter target port.");
            return false;
        }

        if (bufferSizeInput.getText().length() == 0) {
            Dialogs.createErrorDialog("Buffer size required", "You have to enter buffer size.");
            return false;
        }

        if (charsetInput.getText().length() == 0) {
            Dialogs.createErrorDialog("Charset required", "You have to enter charset.");
            return false;
        }

        if (!Charset.isSupported(charsetInput.getText())) {
            Dialogs.createErrorDialog("Charset not supported", "You have entered unsupported charset.");
            return false;
        }

        if (connectionCloseDelayInput.getText().length() == 0) {
            Dialogs.createErrorDialog("Connection close delay required",
                    "You have to enter connection close delay.");
            return false;
        }

        return true;
    }

    /**
     * Lets user choose file with server certificate.
     */
    @FXML
    private void onServerCertificateOpenButtonClick(ActionEvent event) {
        // Choose server certificate file
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Server certificate file");

        if (serverKeyStoreInput.getText().isBlank()) {
            fileChooser.setInitialDirectory(new File(FileUtils.getProjectDirectory()));
        } else {
            var temp = new File(FileUtils.getProjectFileAbsolutePath(serverKeyStoreInput.getText()));
            fileChooser.setInitialDirectory(temp.getParentFile());
            fileChooser.setInitialFileName(temp.getName());
        }

        var file = fileChooser.showSaveDialog(null);
        if (file == null) {
            return;
        }

        serverKeyStoreInput.setText(Paths.get(FileUtils.getProjectDirectory())
                .toAbsolutePath()
                .relativize(Paths.get(file.getAbsolutePath()))
                .toString()
                .replace('\\', '/'));
    }

    /**
     * Lets user choose file with client certificate.
     */
    @FXML
    private void onClientCertificateOpenButtonClick(ActionEvent event) {
        // Choose client certificate file
        var fileChooser = new FileChooser();
        fileChooser.setTitle("Client certificate file");

        if (clientKeyStoreInput.getText().isBlank()) {
            fileChooser.setInitialDirectory(new File(FileUtils.getProjectDirectory()));
        } else {
            var temp = new File(FileUtils.getProjectFileAbsolutePath(clientKeyStoreInput.getText()));
            fileChooser.setInitialDirectory(temp.getParentFile());
            fileChooser.setInitialFileName(temp.getName());
        }

        var file = fileChooser.showSaveDialog(null);
        if (file == null) {
            return;
        }

        clientKeyStoreInput.setText(Paths.get(FileUtils.getProjectDirectory())
                .toAbsolutePath()
                .relativize(Paths.get(file.getAbsolutePath()))
                .toString()
                .replace('\\', '/'));
    }

    /**
     * On server SSL enabled event.
     */
    private void onServerSslEnabledChange(
            ObservableValue<? extends Toggle> observable,
            Toggle oldValue,
            Toggle newValue) {
        if (serverSslRadio.isSelected()) {
            serverAlgorithmInput.setDisable(false);
            serverKeyStoreInput.setDisable(false);
            serverKeyStoreTypeInput.setDisable(false);
            serverKeyStorePasswordInput.setDisable(false);
            serverKeyPasswordInput.setDisable(false);
        } else {
            serverAlgorithmInput.setDisable(true);
            serverKeyStoreInput.setDisable(true);
            serverKeyStoreTypeInput.setDisable(true);
            serverKeyStorePasswordInput.setDisable(true);
            serverKeyPasswordInput.setDisable(true);
        }
    }

    /**
     * On client SSL enabled event.
     */
    private void onClientSslEnabledChange(
            ObservableValue<? extends Toggle> observable,
            Toggle oldValue,
            Toggle newValue) {
        if (clientSslWithCertificateRadio.isSelected()) {
            clientAlgorithmInput.setDisable(false);
            clientKeyStoreInput.setDisable(false);
            clientKeyStoreTypeInput.setDisable(false);
            clientKeyStorePasswordInput.setDisable(false);
            clientKeyPasswordInput.setDisable(false);
        } else {
            clientAlgorithmInput.setDisable(!clientSslRadio.isSelected());
            clientKeyStoreInput.setDisable(true);
            clientKeyStoreTypeInput.setDisable(true);
            clientKeyStorePasswordInput.setDisable(true);
            clientKeyPasswordInput.setDisable(true);
        }
    }
}
