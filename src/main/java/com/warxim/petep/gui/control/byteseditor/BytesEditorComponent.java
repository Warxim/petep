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
import javafx.scene.control.IndexRange;

import java.nio.charset.Charset;

/**
 * Interface for bytes editor tab wrappers and nested components.
 */
@PetepAPI
public interface BytesEditorComponent {
    /**
     * Sets bytes to the tab.
     * @param bytes Bytes to be set
     * @param size Size of the data in bytes to be set
     * @param charset Charset of the data
     */
    void setBytes(byte[] bytes, int size, Charset charset);

    /**
     * Obtains bytes from the tab.
     * @return Byte array
     */
    byte[] getBytes();

    /**
     * Changes editability of the data.
     * @param value {@code true} if the data in the tab should be editable
     */
    void setEditable(boolean value);

    /**
     * Select bytes in the editor tab
     * @param selectionRange Selection range to use
     */
    void selectBytes(IndexRange selectionRange);

    /**
     * Obtains current bytes selection from editor tab
     * @return Current selection range
     */
    IndexRange getBytesSelection();
}
