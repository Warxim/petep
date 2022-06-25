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

import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.util.BytesUtils;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

/**
 * Hexadecimal editor of bytes.
 * <p>Uses hex representation, for example: 77 61 72 78 69 6D 2E 63 6F 6D</p>
 */
@PetepAPI
public class HexEditorTab extends Tab implements BytesEditorTab {
    private static final Pattern HEX_FORMAT_PATTERN_1 = Pattern.compile("[^\\s0-9A-F]");
    private static final Pattern HEX_FORMAT_PATTERN_2 = Pattern.compile("([0-9A-F][0-9A-F])([0-9A-F])");
    private static final Pattern HEX_FORMAT_PATTERN_3 = Pattern.compile("(^| )([0-9A-F]) ([0-9A-F])($| )");
    private static final Pattern HEX_FORMAT_PATTERN_4 = Pattern.compile("(^| )([0-9A-F])($| )");

    @FXML
    private TextArea textInput;

    /**
     * Constructs hex editor tab.
     * @throws IOException If the template could not be loaded
     */
    public HexEditorTab() throws IOException {
        var loader = new FXMLLoader(getClass().getResource("/fxml/control/TextEditorTab.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.setClassLoader(getClass().getClassLoader());
        loader.load();

        // Automatic formating.
        textInput.focusedProperty().addListener(this::onTextFocusChange);
        textInput.textProperty().addListener(this::onTextPropertyChange);

        setText("Hex");
    }

    @Override
    public void setBytes(byte[] bytes, int size, Charset charset) {
        textInput.setText(BytesUtils.bytesToHexString(bytes, size));
    }

    @Override
    public byte[] getBytes() {
        return BytesUtils.hexStringToBytes(
                HEX_FORMAT_PATTERN_4.matcher(textInput.getText()).replaceAll("$10$2$3"));
    }

    @Override
    public void setEditable(boolean value) {
        textInput.setEditable(value);
    }

    /**
     * Formats the text input when focus changes.
     */
    private void onTextFocusChange(ObservableValue<? extends Boolean> observable, boolean oldValue, boolean newValue) {
        if (oldValue) {
            var text = HEX_FORMAT_PATTERN_4.matcher(textInput.getText()).replaceAll("$10$2$3");
            var bytes = BytesUtils.hexStringToBytes(text);
            setBytes(bytes, bytes.length, null);
        }
    }

    /**
     * Formats the input when something is added to it.
     */
    private void onTextPropertyChange(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        var newText = HEX_FORMAT_PATTERN_1.matcher(newValue.toUpperCase()).replaceAll("");
        newText = HEX_FORMAT_PATTERN_2.matcher(newText).replaceAll("$1 $2");
        newText = HEX_FORMAT_PATTERN_3.matcher(newText).replaceAll("$1$2$3$4");
        newText = newText.replace("  ", " ");
        if (newText.equals(newValue)) {
            return;
        }

        textInput.setText(newText);
    }
}
