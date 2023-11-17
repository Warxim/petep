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
package com.warxim.petep.gui.control.byteseditor;

import com.warxim.petep.common.Constant;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.gui.dialog.Dialogs;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JavaFX byte array editor.
 * <p>Simple editor for editing byte arrays with charset support.</p>
 * <p>Editability can be switched off, so that this editor behaves like viewer.</p>
 * <p>Contains multiple tabs with different approaches for byte editing.</p>
 */
@PetepAPI
public class BytesEditor extends AnchorPane {
    private final BooleanProperty editable = new SimpleBooleanProperty(this, "editable", true);

    protected Charset charset;
    protected byte[] bytes;

    @FXML
    protected TabPane tabs;
    @FXML
    protected Label charsetLabel;
    @FXML
    protected Label infoLabel;

    /**
     * Constructs byte editor.
     * @throws IOException If the template could not be loaded
     */
    public BytesEditor() throws IOException {
        var loader = new FXMLLoader(getClass().getResource("/fxml/control/BytesEditor.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.setClassLoader(getClass().getClassLoader());
        loader.load();

        charset = Constant.DEFAULT_CHARSET;

        charsetLabel.setText(charset.toString());

        tabs.getSelectionModel().selectedItemProperty().addListener(this::onTabChange);

        tabs.getTabs().add(new TextEditorTab());
        tabs.getTabs().add(new HexEditorTab());

        editable.addListener(this::onEditablePropertyChange);
    }

    /**
     * Sets data into the editor (shows only limited number of bytes - by size).
     * @param bytes Byte buffer to be set to the editor
     * @param size Size of the data in the buffer
     * @param charset Charset of the data in the buffer
     */
    public void setData(byte[] bytes, int size, Charset charset) {
        this.bytes = bytes;
        this.charset = charset;

        var currentTab = (BytesEditorComponent) tabs.getSelectionModel().getSelectedItem();
        if (currentTab != null) {
            currentTab.setBytes(bytes, size, charset);
        }

        charsetLabel.setText(charset.displayName());
    }

    /**
     * Sets data into the editor (uses length of the bytes as size).
     * @param bytes Byte buffer to be set to the editor
     * @param charset Charset of the data in the buffer
     */
    public void setData(byte[] bytes, Charset charset) {
        setData(bytes, bytes.length, charset);
    }

    /**
     * Sets bytes into the editor (shows only limited number of bytes - by size).
     * @param bytes Byte buffer to be set to the editor
     * @param size Size of the data in the buffer
     */
    public void setBytes(byte[] bytes, int size) {
        this.bytes = bytes;

        var currentTab = (BytesEditorComponent) tabs.getSelectionModel().getSelectedItem();
        if (currentTab != null) {
            currentTab.setBytes(bytes, size, charset);
        }
    }

    /**
     * Sets bytes into the editor (uses length of the bytes as size).
     * @param bytes Byte buffer to be set to the editor
     */
    public void setBytes(byte[] bytes) {
        setBytes(bytes, bytes.length);
    }

    /**
     * Obtains bytes from the editor.
     * @return Byte array
     */
    public byte[] getBytes() {
        bytes = ((BytesEditorComponent) tabs.getSelectionModel().getSelectedItem()).getBytes();

        return bytes;
    }

    /**
     * Obtains charset used in the editor.
     * @return Charset
     */
    public Charset getCharset() {
        return charset;
    }

    /**
     * Sets charset of the data in the editor.
     * @param charset Charset to be set
     */
    public void setCharset(Charset charset) {
        var currentTab = (BytesEditorComponent) tabs.getSelectionModel().getSelectedItem();
        if (currentTab != null) {
            bytes = currentTab.getBytes();
        }

        this.charset = charset;

        if (currentTab != null) {
            currentTab.setBytes(bytes, bytes.length, charset);
        }

        charsetLabel.setText(charset.displayName());
    }

    /**
     * Clears the editor.
     */
    public void clear() {
        setData(new byte[0], Constant.DEFAULT_CHARSET);
    }

    /**
     * Checks whether the editor is editable.
     * @return {@code true} if the editor is configured as editable
     */
    public final boolean isEditable() {
        return editable.getValue();
    }

    /**
     * Makes the editor editable/uneditable.
     * @param value {@code true} if the editor should be editable;
     *              {@code false} if the editor should act like view
     */
    public final void setEditable(boolean value) {
        editable.setValue(value);
    }

    /**
     * Obtains editable boolean property.
     * @return Editable boolean property
     */
    public final BooleanProperty editableProperty() {
        return editable;
    }

    /**
     * Shows dialog for changing charset of the data.
     */
    @FXML
    protected void onCharsetClick() {
        var dialog = new TextInputDialog(charset.toString());
        dialog.setTitle("Change charset");
        dialog.setHeaderText("Change charset");
        dialog.setContentText("New charset:");

        var maybeCharset = dialog.showAndWait();
        if (maybeCharset.isEmpty()) {
            return;
        }

        var newCharset = maybeCharset.get();
        if (!Charset.isSupported(newCharset)) {
            Dialogs.createErrorDialog("Charset not supported", "Specified charset is not supported!");
            return;
        }

        setCharset(Charset.forName(newCharset));
    }

    /**
     * Changes editable property of tabs when editable property of byte editor changes.
     */
    protected void onEditablePropertyChange(
            ObservableValue<? extends Boolean> observable,
            Boolean oldValue,
            Boolean newValue) {
        tabs.getTabs().forEach(tab -> ((BytesEditorComponent) tab).setEditable(newValue));
    }

    /**
     * Sets bytes to newly opened tab.
     */
    protected void onTabChange(ObservableValue<? extends Tab> observable, Tab oldTab, Tab newTab) {
        IndexRange bytesSelection = null;
        if (oldTab != null) {
            var component = ((BytesEditorComponent) oldTab);
            bytes = component.getBytes();
            bytesSelection = component.getBytesSelection();
            component.getInfoProperty().removeListener(this::onInfoMapChange);
            component.getFocusedProperty().removeListener(this::onFocusChange);
        }

        if (newTab != null) {
            var component = ((BytesEditorComponent) newTab);

            if (bytes != null) {
                component.setBytes(bytes, bytes.length, charset);
                component.selectBytes(bytesSelection);
            }

            component.getInfoProperty().addListener(this::onInfoMapChange);
            setInfo(component.getInfoProperty());
            component.getFocusedProperty().addListener(this::onFocusChange);
        }
    }

    /**
     * Handles change of focus in the editor tabs
     */
    private void onFocusChange(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        setFocused(newValue);
    }

    /**
     * Handles change of editor info from the tab
     */
    private void onInfoMapChange(MapChangeListener.Change<? extends String, ? extends String> change) {
        setInfo(change.getMap());
    }

    /**
     * Displays editor info from the tab in the info label
     */
    private void setInfo(Map<? extends String, ? extends String> info) {
        var newInfo = info.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
        infoLabel.setText(newInfo);
    }
}
