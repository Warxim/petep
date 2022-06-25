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
package com.warxim.petep.extension.internal.externalhttpproxy.lighthttp.server;

import com.warxim.petep.common.Constant;
import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.externalhttpproxy.lighthttp.LightHttpConstant;
import com.warxim.petep.extension.internal.externalhttpproxy.lighthttp.LightHttpUtils;
import com.warxim.petep.extension.internal.externalhttpproxy.lighthttp.exception.InvalidDataException;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.worker.Interceptor;
import com.warxim.petep.proxy.worker.Proxy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Light HTTP PDU reader, which is used to read and deserialize PDUs from input stream.
 */
public class LightHttpPduReader {
    private final PetepHelper petepHelper;
    private final int lastInterceptorC2SIndex;
    private final int lastInterceptorS2CIndex;

    /**
     * Constructs light HTTP PDU reader.
     * @param petepHelper Helper for accessing currently running core
     */
    public LightHttpPduReader(PetepHelper petepHelper) {
        this.petepHelper = petepHelper;
        this.lastInterceptorC2SIndex = petepHelper.getInterceptorsC2S().size() + 1;
        this.lastInterceptorS2CIndex = petepHelper.getInterceptorsS2C().size() + 1;
    }

    /**
     * Reads and deserialized PDU from the given input stream.
     * @param in Input stream for reading the PDU
     * @return pdu Deserialized PDU
     * @throws IOException If there is problem with reading the PDU from the output stream
     * @throws InvalidDataException If the deserialized PDU or the request itself is not valid
     */
    public PDU read(InputStream in) throws IOException, InvalidDataException {
        // Skip POST /
        LightHttpUtils.skipUntil(in, LightHttpConstant.SLASH);

        var builder = new StringBuilder();

        // Read destination
        var destination = readDestination(in);
        // Proxy
        var proxy = readProxy(in, builder);
        // Connection
        builder.setLength(0);
        var connection = readConnection(in, builder, proxy);
        // Target interceptor
        builder.setLength(0);
        var targetInterceptorId = readTargetInterceptor(in, builder);

        // Check validity of interceptor id.
        validateTargetInterceptor(destination, targetInterceptorId);

        // Skip HTTP/1.0\r\n
        LightHttpUtils.skipUntil(in, LightHttpConstant.LF);

        // Read headers
        var serializedMetaData = new HashMap<String, String>();
        var tags = new HashSet<String>();
        var charset = Constant.DEFAULT_CHARSET;
        var contentLength = 0;

        int currentByte;

        while (true) {
            // Read first character to detect end of headers
            currentByte = LightHttpUtils.readByte(in);

            if (currentByte == LightHttpConstant.CR) {
                // skip \n\r\n
                LightHttpUtils.skipNBytes(in, 1);
                break;
            }

            // Read key
            builder.setLength(0);
            builder.append((char) currentByte);
            LightHttpUtils.appendUntil(in, builder, LightHttpConstant.COLON);

            var temp = builder.toString();

            // Skip space after :
            LightHttpUtils.skipNBytes(in, 1);

            builder.setLength(0);

            if (temp.equals(LightHttpConstant.CONTENT_LENGTH_HEADER)) {
                contentLength = readContentLengthValue(in, builder);
            } else if (temp.equals(LightHttpConstant.CONTENT_TYPE_HEADER)) {
                charset = readCharset(in, builder);
            } else if (temp.equals(LightHttpConstant.TAGS_HEADER)) {
                readTagsValue(in, builder, tags);
            } else if (temp.startsWith(LightHttpConstant.METADATA_HEADER_START)) {
                serializedMetaData.put(temp.substring(2), readMetadataValue(in, builder));
            } else {
                skipHeaderValue(in);
            }

            // Skip \n
            LightHttpUtils.skipNBytes(in, 1);
        }

        // Read data.
        var buffer = in.readNBytes(contentLength);

        // Create PDU.
        var maybePdu = proxy.getModule().getFactory().getDeserializer()
                .deserializePdu(proxy, connection, destination, buffer, contentLength, charset, tags, serializedMetaData);
        if (maybePdu.isEmpty()) {
            throw new InvalidDataException(LightHttpConstant.RESPONSE_INVALID_PDU);
        }
        var pdu = maybePdu.get();

        // Set last interceptor for correct forwarding
        if (targetInterceptorId != 0) {
            Interceptor lastInterceptor;
            if (destination == PduDestination.SERVER) {
                lastInterceptor = petepHelper.getInterceptorsC2S().get(targetInterceptorId - 1);
            } else {
                lastInterceptor = petepHelper.getInterceptorsS2C().get(targetInterceptorId - 1);
            }
            pdu.setLastInterceptor(lastInterceptor);
        }

        return pdu;
    }

    /**
     * Reads proxy from input stream.
     */
    private Proxy readProxy(InputStream in, StringBuilder builder)
            throws IOException, InvalidDataException {
        // Read proxy code
        LightHttpUtils.skipNBytes(in, LightHttpConstant.PROXY.length + 1);
        LightHttpUtils.appendUntil(in, builder, LightHttpConstant.SLASH);

        return petepHelper.getProxy(builder.toString())
                .orElseThrow(() ->  new InvalidDataException(LightHttpConstant.RESPONSE_WRONG_PROXY));
    }

    /**
     * Reads connection from input stream.
     */
    private Connection readConnection(InputStream in, StringBuilder builder, Proxy proxy)
            throws IOException, InvalidDataException {
        // Read client ID
        LightHttpUtils.skipNBytes(in, LightHttpConstant.CONNECTION.length + 1);
        LightHttpUtils.appendUntil(in, builder, LightHttpConstant.SLASH);

        return proxy.getConnectionManager()
                .get(builder.toString())
                .orElseThrow(() -> new InvalidDataException(LightHttpConstant.RESPONSE_WRONG_CONNECTION));
    }

    /**
     * Reads destination from input stream.
     */
    private PduDestination readDestination(InputStream in) throws IOException {
        PduDestination destination;

        LightHttpUtils.skipNBytes(in, LightHttpConstant.DESTINATION.length + 1);
        if (LightHttpUtils.readByte(in) == LightHttpConstant.CLIENT[0]) {
            destination = PduDestination.CLIENT;
        } else {
            destination = PduDestination.SERVER;
        }
        LightHttpUtils.skipUntil(in, LightHttpConstant.SLASH);

        return destination;
    }

    /**
     * Reads target interceptor from input stream.
     */
    private int readTargetInterceptor(InputStream in, StringBuilder builder) throws IOException {
        // Read target interceptor id
        LightHttpUtils.skipNBytes(in, LightHttpConstant.INTERCEPTOR.length + 1);
        LightHttpUtils.appendUntil(in, builder, LightHttpConstant.SPACE);
        return Integer.parseInt(builder.toString());
    }

    /**
     * Validates that the target interceptor ID is in required range.
     */
    private void validateTargetInterceptor(PduDestination destination, int targetInterceptorId)
            throws InvalidDataException {
        var invalid = targetInterceptorId < 0
                || (destination == PduDestination.SERVER && targetInterceptorId >= lastInterceptorC2SIndex)
                || (destination == PduDestination.CLIENT && targetInterceptorId >= lastInterceptorS2CIndex);
        if (invalid) {
            throw new InvalidDataException(LightHttpConstant.RESPONSE_WRONG_INTERCEPTOR);
        }
    }

    /**
     * Skips header value.
     */
    private void skipHeaderValue(InputStream in) throws IOException {
        // Unknown header, skip value.
        LightHttpUtils.skipUntil(in, LightHttpConstant.CR);
    }

    /**
     * Reads content length header value.
     */
    private int readContentLengthValue(InputStream in, StringBuilder builder) throws IOException {
        // Read until \r
        LightHttpUtils.appendUntil(in, builder, LightHttpConstant.CR);
        return Integer.parseInt(builder.toString());
    }

    /**
     * Reads metadata header value.
     */
    private String readMetadataValue(InputStream in, StringBuilder builder) throws IOException {
        // Read until \r
        LightHttpUtils.appendUntil(in, builder, LightHttpConstant.CR);
        return builder.toString();
    }

    /**
     * Reads tags header value.
     */
    private void readTagsValue(InputStream in, StringBuilder builder, Set<String> tags)
            throws IOException {
        int currentByte;
        // Read until \r
        while ((currentByte = LightHttpUtils.readByte(in)) != LightHttpConstant.CR) {
            if (currentByte == LightHttpConstant.TAGS_SEPARATOR) {
                tags.add(builder.toString());
                builder.setLength(0);
            } else {
                builder.append((char) currentByte);
            }
        }

        tags.add(builder.toString());
    }

    /**
     * Reads charset from content-type header value.
     */
    private Charset readCharset(InputStream in, StringBuilder builder) throws IOException {
        // Skip text/plain; charset=
        LightHttpUtils.skipNBytes(in, 20);

        // Read until 0x0D = \r
        LightHttpUtils.appendUntil(in, builder, LightHttpConstant.CR);

        return Charset.forName(builder.toString());
    }
}
