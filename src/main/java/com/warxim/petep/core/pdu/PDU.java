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
 * PDU (Protocol Data Unit) is core unit for data processed in PETEP.
 */
@PetepAPI
public abstract class PDU {
    /**
     * Parent proxy.
     */
    protected Proxy proxy;

    /**
     * Parent connection.
     */
    protected Connection connection;

    /**
     * PDU destination.
     */
    protected PduDestination destination;

    /**
     * Interceptor in which was the PDU processed the last time.
     */
    protected Interceptor lastInterceptor;

    /**
     * PDU tags.
     */
    protected Set<String> tags;

    /**
     * Creates PDU with specified arguments
     * @param proxy Proxy
     * @param connection Connection
     * @param destination Destination of the PDU
     * @param tags Set of tags
     *             <p><b>Warning:</b> The set of tags should be mutable, so that the PDU can be tagged afterwards.</p>
     */
    protected PDU(Proxy proxy, Connection connection, PduDestination destination, Set<String> tags) {
        this.proxy = proxy;
        this.destination = destination;
        this.connection = connection;
        this.tags = tags;
    }

    /**
     * Checks if the PDU contain specified tag.
     * @param tag Tag to be checked
     * @return {@code true} if the PDU has the specified tag
     */
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    /**
     * Adds tag to the PDU.
     * @param tag Tag to be added
     */
    public void addTag(String tag) {
        tags.add(tag);
    }

    /**
     * Removes tag from the PDU.
     * @param tag Tag to be removed
     */
    public void removeTag(String tag) {
        tags.remove(tag);
    }

    /**
     * Adds tags to the PDU.
     * @param tags Tags to be added to the PDU
     */
    public void addTags(Collection<String> tags) {
        this.tags.addAll(tags);
    }

    /**
     * Obtains tags.
     * @return Set of tags
     */
    public Set<String> getTags() {
        return tags;
    }

    /**
     * Obtains parent proxy.
     * @return Proxy from which this PDU is coming
     */
    public Proxy getProxy() {
        return proxy;
    }

    /**
     * Sets parent proxy.
     * @param proxy Proxy to be set
     */
    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    /**
     * Obtains PDU destination.
     * @return Destination of the PDU
     */
    public PduDestination getDestination() {
        return destination;
    }

    /**
     * Sets PDU destination.
     * @param destination Destination of the PDU
     */
    public void setDestination(PduDestination destination) {
        this.destination = destination;
    }

    /**
     * Obtains connection.
     * @return Connection that will handle sending of this PDU
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Sets connection.
     * @param connection Connection that will handle sending of this PDU
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * Obtains last interceptor.
     * @return Last interceptor that processed this PDU
     */
    public Interceptor getLastInterceptor() {
        return lastInterceptor;
    }

    /**
     * Sets last interceptor.
     * @param lastInterceptor Last interceptor that processed this PDU
     */
    public void setLastInterceptor(Interceptor lastInterceptor) {
        this.lastInterceptor = lastInterceptor;
    }

    /**
     * Get the data buffer.
     * @return The whole byte buffer used to store data (can be bigger that the data inside it)
     */
    public abstract byte[] getBuffer();

    /**
     * Get size of data in the buffer.
     * @return Size of the data
     */
    public abstract int getSize();

    /**
     * Set the buffer
     * @param buffer new byte array buffer to be used
     * @param size size of the data in provided buffer
     */
    public abstract void setBuffer(byte[] buffer, int size);

    /**
     * Resizes the buffer.
     * @param size New size of the buffer
     */
    public abstract void resize(int size);

    /**
     * Get the charset of the PDU.
     * @return Charset of the data in the buffer
     */
    public abstract Charset getCharset();

    /**
     * Sets the charset of the PDU.
     * <p><b>Note:</b> Does not convert the data, just sets the charset property of PDU.</p>
     * @param charset Charset of the PDU
     */
    public abstract void setCharset(Charset charset);

    /**
     * Creates deep copy of the PDU.
     * @return Deep copy of the PDU
     */
    public abstract PDU copy();
}
