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
package com.warxim.petep.core.pdu;

import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.interceptor.worker.Interceptor;
import com.warxim.petep.proxy.worker.Proxy;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Set;

/**
 * PDU (Protocol Data Unit) is core unit for data processed in PETEP
 */
@PetepAPI
public interface PDU {
    /**
     * Checks if the PDU contain specified tag.
     * @param tag Tag to be checked
     * @return {@code true} if the PDU has the specified tag
     */
    boolean hasTag(String tag);

    /**
     * Adds tag to the PDU.
     * @param tag Tag to be added
     */
    void addTag(String tag);

    /**
     * Removes tag from the PDU.
     * @param tag Tag to be removed
     */
    void removeTag(String tag);

    /**
     * Adds tags to the PDU.
     * @param tags Tags to be added to the PDU
     */
    void addTags(Collection<String> tags);

    /**
     * Obtains tags.
     * @return Set of tags
     */
    Set<String> getTags();

    /**
     * Obtains parent proxy.
     * @return Proxy from which this PDU is coming
     */
    Proxy getProxy();

    /**
     * Sets parent proxy.
     * @param proxy Proxy to be set
     */
    void setProxy(Proxy proxy);

    /**
     * Obtains PDU destination.
     * @return Destination of the PDU
     */
    PduDestination getDestination();

    /**
     * Sets PDU destination.
     * @param destination Destination of the PDU
     */
    void setDestination(PduDestination destination);

    /**
     * Obtains connection.
     * @return Connection that will handle sending of this PDU
     */
    Connection getConnection();

    /**
     * Sets connection.
     * @param connection Connection that will handle sending of this PDU
     */
    void setConnection(Connection connection);

    /**
     * Obtains last interceptor.
     * @return Last interceptor that processed this PDU
     */
    Interceptor getLastInterceptor();

    /**
     * Sets last interceptor.
     * @param lastInterceptor Last interceptor that processed this PDU
     */
    void setLastInterceptor(Interceptor lastInterceptor);

    /**
     * Get the data buffer.
     * @return The whole byte buffer used to store data (can be bigger that the data inside it)
     */
    byte[] getBuffer();

    /**
     * Get size of data in the buffer.
     * @return Size of the data
     */
    int getSize();

    /**
     * Set the buffer
     * @param buffer new byte array buffer to be used
     * @param size size of the data in provided buffer
     */
    void setBuffer(byte[] buffer, int size);

    /**
     * Resizes the buffer.
     * @param size New size of the buffer
     */
    void resize(int size);

    /**
     * Get the charset of the PDU.
     * @return Charset of the data in the buffer
     */
    Charset getCharset();

    /**
     * Sets the charset of the PDU.
     * <p><b>Note:</b> Does not convert the data, just sets the charset property of PDU.</p>
     * @param charset Charset of the PDU
     */
    void setCharset(Charset charset);

    /**
     * Creates deep copy of the PDU.
     * @return Deep copy of the PDU
     */
    PDU copy();
}
