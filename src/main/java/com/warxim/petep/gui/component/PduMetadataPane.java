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
package com.warxim.petep.gui.component;

import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.gui.common.GuiConstant;
import com.warxim.petep.proxy.worker.Proxy;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.Set;

/**
 * Configuration pane for PDU meta data configuration.
 * <p>Can be used to create custom controls for specific PDU types.</p>
 */
@PetepAPI
public abstract class PduMetadataPane extends AnchorPane {
    /**
     * Creates configuration pane from specified template and sets the object as controller.
     * @param template Path to FXML template
     * @throws IOException If the template could not be loaded
     */
    protected PduMetadataPane(String template) throws IOException {
        var loader = new FXMLLoader(getClass().getResource(template));
        loader.setRoot(this);
        loader.setController(this);
        loader.load();
        getStylesheets().add(GuiConstant.MAIN_CSS_PATH);
    }

    /**
     * Creates PDU using provided data and data in metadata pane inputs.
     * @param proxy Proxy of the PDU
     * @param connection Connection for sending the PDU
     * @param destination Destination of the PDU
     * @param buffer Data buffer
     * @param size Size of the data in the buffer
     * @param charset Charset of the data in the buffer
     * @param tags Set of PDU tags
     * @return Created PDU
     */
    public abstract Optional<PDU> createPdu(
            Proxy proxy,
            Connection connection,
            PduDestination destination,
            byte[] buffer,
            int size,
            Charset charset,
            Set<String> tags);

    /**
     * Sets PDU to the pane.
     * @param pdu PDU to be set
     */
    public abstract void setPdu(PDU pdu);

    /**
     * Clears metadata content.
     */
    public abstract void clear();

    /**
     * Checks whether the metadata are valid.
     * @return {@code true} if metadata are valid
     */
    public abstract boolean isValid();

}
