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

import com.warxim.petep.extension.PetepAPI;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Tab;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Hexadecimal editor of bytes.
 * <p>Uses hex representation, for example: 77 61 72 78 69 6D 2E 63 6F 6D</p>
 */
@PetepAPI
public class HexEditorTab extends Tab implements BytesEditorComponent {
    @FXML
    private HexEditor hexInput;

    /**
     * Constructs hex editor tab.
     * @throws IOException If the template could not be loaded
     */
    public HexEditorTab() throws IOException {
        var loader = new FXMLLoader(getClass().getResource("/fxml/control/HexEditorTab.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        loader.setClassLoader(getClass().getClassLoader());
        loader.load();

        setText("Hex");
    }

    @Override
    public void setBytes(byte[] bytes, int size, Charset charset) {
        hexInput.setBytes(bytes, size, charset);
    }

    @Override
    public byte[] getBytes() {
        return hexInput.getBytes();
    }

    @Override
    public void setEditable(boolean value) {
        hexInput.setEditable(value);
    }

    @Override
    public void selectBytes(IndexRange selectionRange) {
        hexInput.selectBytes(selectionRange);
    }

    @Override
    public IndexRange getBytesSelection() {
        return hexInput.getBytesSelection();
    }
}
