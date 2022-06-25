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

import com.warxim.petep.common.Constant;
import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.proxy.worker.Proxy;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

/**
 * Default PDU implementation.
 * <p>Uses simple byte array for data buffer.</p>
 * <p>By default uses charset specified in {@link Constant#DEFAULT_CHARSET}.</p>
 */
@PetepAPI
public class DefaultPdu extends PDU {
    /**
     * Data buffer
     */
    protected byte[] buffer;
    /**
     * Size of the data in the buffer
     */
    protected int size;
    /**
     * Charset of the data in the buffer
     */
    protected Charset charset;

    /**
     * Constructs default PDU.
     * @param proxy Proxy
     * @param connection Connection
     * @param destination Destination of the PDU
     * @param buffer Data buffer
     * @param size Size of the data in the buffer
     * @param charset Charset of the data in the buffer
     * @param tags Set of tags
     */
    public DefaultPdu(Proxy proxy, Connection connection, PduDestination destination, byte[] buffer, int size, Charset charset, Set<String> tags) {
        super(proxy, connection, destination, tags);
        this.buffer = buffer;
        this.size = size;
        this.charset = charset;
    }

    /**
     * Constructs default PDU with default charset.
     * @param proxy Proxy
     * @param connection Connection
     * @param destination Destination of the PDU
     * @param buffer Data buffer
     * @param size Size of the data in the buffer
     * @param tags Set of tags
     */
    public DefaultPdu(Proxy proxy, Connection connection, PduDestination destination, byte[] buffer, int size, Set<String> tags) {
        this(proxy, connection, destination, buffer, size, Constant.DEFAULT_CHARSET, tags);
    }

    /**
     * Constructs default PDU with empty tag set.
     * @param proxy Proxy
     * @param connection Connection
     * @param destination Destination of the PDU
     * @param buffer Data buffer
     * @param size Size of the data in the buffer
     * @param charset Charset of the data in the buffer
     */
    public DefaultPdu(Proxy proxy, Connection connection, PduDestination destination, byte[] buffer, int size, Charset charset) {
        this(proxy, connection, destination, buffer, size, charset, new HashSet<>());
    }

    /**
     * Constructs default PDU with default charset and empty tag set.
     * @param proxy Proxy
     * @param connection Connection
     * @param destination Destination of the PDU
     * @param buffer Data buffer
     * @param size Size of the data in the buffer
     */
    public DefaultPdu(Proxy proxy, Connection connection, PduDestination destination, byte[] buffer, int size) {
        this(proxy, connection, destination, buffer, size, Constant.DEFAULT_CHARSET, new HashSet<>());
    }

    @Override
    public byte[] getBuffer() {
        return buffer;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public Charset getCharset() {
        return charset;
    }

    @Override
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    @Override
    public void setBuffer(byte[] buffer, int size) {
        this.buffer = buffer;
        this.size = size;
    }

    /**
     * Resizes the buffer (expands the buffer when needed, but does not shrink the buffer).
     * <p>Creates new byte array and copies existing buffer to it, if the buffer has to be expanded.</p>
     * @param size Size of the data buffer
     */
    @Override
    public void resize(int size) {
        if (size <= buffer.length) {
            return;
        }

        byte[] newBuffer = new byte[size];

        // Copy content of original buffer to new one.
        System.arraycopy(buffer, 0, newBuffer, 0, this.size);

        buffer = newBuffer;
    }

    @Override
    public PDU copy() {
        var pdu = new DefaultPdu(proxy, connection, destination, buffer.clone(), size, charset);

        pdu.addTags(tags);
        pdu.setLastInterceptor(pdu.getLastInterceptor());

        return pdu;
    }
}
