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
package com.warxim.petep.gui.control;

import com.warxim.petep.core.pdu.SerializedPdu;
import com.warxim.petep.extension.PetepAPI;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Serialized PDU view for displaying {@link SerializedPdu}.
 */
@PetepAPI
public class SerializedPduView extends AnchorPane {
    private SerializedPdu serializedPdu;

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
    private TextField tagsField;
    @FXML
    private BytesEditor bytesEditor;
    @FXML
    private TextArea metadataArea;

    /**
     * Constructs view without any PDU.
     * @throws IOException If the template could not be loaded
     */
    public SerializedPduView() throws IOException {
        var loader = new FXMLLoader(getClass().getResource("/fxml/control/SerializedPduView.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.setClassLoader(getClass().getClassLoader());
        loader.load();
    }

    /**
     * Sets PDU to pane.
     * @param serializedPdu Serialized PDU to be displayed
     */
    public void setSerializedPdu(SerializedPdu serializedPdu) {
        this.serializedPdu = serializedPdu;

        if (serializedPdu.getProxy() != null) {
            proxyField.setText(serializedPdu.getProxy());
        }

        if (serializedPdu.getConnection() != null) {
            connectionField.setText(serializedPdu.getConnection());
        }

        if (serializedPdu.getDestination() != null) {
            destinationField.setText(serializedPdu.getDestination().name());
        }

        if (serializedPdu.getInterceptor() != null) {
            interceptorField.setText(serializedPdu.getInterceptor());
        }

        if (serializedPdu.getTags() != null) {
            tagsField.setText(tagsToString(serializedPdu.getTags()));
        }

        if (serializedPdu.getBuffer() != null) {
            bytesEditor.setData(serializedPdu.getBuffer(), serializedPdu.getCharset());
            sizeField.setText(String.valueOf(serializedPdu.getBuffer().length));
        }

        if (serializedPdu.getMetadata() != null) {
            metadataArea.setText(metadataToString(serializedPdu.getMetadata()));
        }
    }

    /**
     * Obtains serialized PDU, which is currently displayed in the view.
     * @return Serialized PDU
     */
    public Optional<SerializedPdu> getSerializedPdu() {
        return Optional.ofNullable(serializedPdu);
    }

    /**
     * Clears the view.
     */
    public void clear() {
        serializedPdu = null;
        proxyField.setText("");
        connectionField.setText("");
        destinationField.setText("");
        interceptorField.setText("");
        tagsField.setText("");
        bytesEditor.clear();
        sizeField.setText("");
        metadataArea.setText("");
    }

    /**
     * Converts metadata to formatted string.
     */
    protected String metadataToString(Map<String, String> metadata) {
        if (metadata.size() == 0) {
            return "";
        }

        int labelLength = metadata.keySet().stream().map(String::length).max(Integer::compareTo).get();
        var builder = new StringBuilder();
        metadata.forEach((key, value) -> {
            builder.append(key);
            builder.append(": ");
            builder.append(" ".repeat(Math.max(0, labelLength - key.length())));
            builder.append(value);
            builder.append('\n');
        });
        return builder.toString();
    }

    /**
     * Converts tags to formatted string.
     */
    protected String tagsToString(Set<String> tags) {
        var tagJoiner = new StringJoiner(", ");
        tags.forEach(tagJoiner::add);
        return tagJoiner.toString();
    }

}
