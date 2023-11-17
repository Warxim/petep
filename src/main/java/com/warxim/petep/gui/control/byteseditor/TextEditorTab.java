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
import javafx.beans.property.MapProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Tab;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Text editor of bytes.
 * <p>Uses text representation of bytes (depending on the charset).</p>
 * <p>
 *     Might not work correctly with some charsets, because JavaFX TextArea removes them.
 *     Therefore it is recommended to use default charset ({@link Constant#DEFAULT_CHARSET}).
 * </p>
 */
@PetepAPI
public class TextEditorTab extends Tab implements BytesEditorComponent {

    @FXML
    private TextEditor textInput;

    /**
     * Constructs text editor tab.
     * @throws IOException If the template could not be loaded
     */
    public TextEditorTab() throws IOException {
        var loader = new FXMLLoader(getClass().getResource("/fxml/control/TextEditorTab.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.setClassLoader(getClass().getClassLoader());
        loader.load();

        setText("Text");
    }

    @Override
    public void setBytes(byte[] bytes, int size, Charset charset) {
        textInput.setBytes(bytes, size, charset);
    }

    @Override
    public byte[] getBytes() {
        return textInput.getBytes();
    }

    @Override
    public void setEditable(boolean value) {
        textInput.setEditable(value);
    }

    @Override
    public void selectBytes(IndexRange selectionRange) {
        textInput.selectBytes(selectionRange);
    }

    @Override
    public IndexRange getBytesSelection() {
        return textInput.getBytesSelection();
    }

    @Override
    public MapProperty<String, String> getInfoProperty() {
        return textInput.getInfoProperty();
    }

    @Override
    public ReadOnlyBooleanProperty getFocusedProperty() {
        return textInput.getFocusedProperty();
    }
}
