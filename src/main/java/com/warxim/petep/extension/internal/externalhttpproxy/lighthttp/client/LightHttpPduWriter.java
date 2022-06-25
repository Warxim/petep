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
package com.warxim.petep.extension.internal.externalhttpproxy.lighthttp.client;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.externalhttpproxy.lighthttp.LightHttpConstant;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

/**
 * Light HTTP PDU writer, which is used to serialize and write PDUs into output stream.
 */
public class LightHttpPduWriter {
    private final int targetInterceptorId;
    private final byte[] firstLineStart;

    /**
     * Constructs light HTTP PDU writer.
     * @param targetInterceptorId Identifier of interceptor to which to send PDUs over HTTP
     * @param serverIp IP address of the HTTP server
     * @param serverPort Port of the HTTP server
     */
    public LightHttpPduWriter(int targetInterceptorId, String serverIp, int serverPort) {
        this.targetInterceptorId = targetInterceptorId;

        firstLineStart = generateFirstLineStart(serverIp, serverPort);
    }

    /**
     * Serializes and writes the given PDU into output stream.
     * @param pdu PDU to be serialized and written to output stream
     * @param out Output stream for writing the PDU
     * @throws IOException If there is problem with writing the PDU into the output stream
     */
    public void write(PDU pdu, OutputStream out) throws IOException {
        // First line
        writeFirstLineToOutput(pdu, out);

        // Tags
        writeTagsToOutput(pdu.getTags(), out);

        // Metadata
        writeMetadataToOutput(pdu.getProxy().getModule().getFactory().getSerializer().serializePduMetadata(pdu), out);

        // Content length
        out.write(LightHttpConstant.CONTENT_LENGTH);
        out.write(String.valueOf(pdu.getSize()).getBytes(StandardCharsets.ISO_8859_1));
        out.write(LightHttpConstant.HEADER_END);

        // Content type
        out.write(LightHttpConstant.CONTENT_TYPE_CHARSET);
        out.write(pdu.getCharset().name().getBytes(StandardCharsets.ISO_8859_1));

        // End headers
        out.write(LightHttpConstant.HEADERS_END);

        // Data
        out.write(pdu.getBuffer(), 0, pdu.getSize());
    }

    /**
     * Generates the start of the first line of the HTTP request.
     * @return Byte representation of the following: POST http://{serverIp}:{serverPort}/
     */
    private byte[] generateFirstLineStart(String serverIp, int serverPort) {
        var serverIpBytes = serverIp.getBytes(StandardCharsets.ISO_8859_1);
        var serverPortBytes = String.valueOf(serverPort).getBytes(StandardCharsets.ISO_8859_1);

        var buffer = ByteBuffer.allocate(
                LightHttpConstant.FIRST_LINE_START.length
                        + 2
                        + serverIpBytes.length
                        + serverPortBytes.length);
        buffer.put(LightHttpConstant.FIRST_LINE_START);
        buffer.put(serverIpBytes);
        buffer.put(LightHttpConstant.COLON);
        buffer.put(serverPortBytes);
        buffer.put(LightHttpConstant.SLASH);

        return buffer.array();
    }

    /**
     * Writes the whole first line of the HTTP request to the output stream.
     * <p>
     *     Example:
     *     POST
     *     http://{serverIp}:{serverPort}/destination/{pdu.destination}/proxy/{pdu.proxy.code}/connection/{pdu.connection.code}/interceptor/{pdu.interceptor.code}
     *     HTTP/1.0
     * </p>
     */
    private void writeFirstLineToOutput(PDU pdu, OutputStream out) throws IOException {
        // First line start
        out.write(firstLineStart);

        // Destination
        out.write(LightHttpConstant.DESTINATION);
        out.write(LightHttpConstant.SLASH);
        if (pdu.getDestination() == PduDestination.CLIENT) {
            out.write(LightHttpConstant.CLIENT);
        } else {
            out.write(LightHttpConstant.SERVER);
        }
        out.write(LightHttpConstant.SLASH);

        // Proxy
        out.write(LightHttpConstant.PROXY);
        out.write(LightHttpConstant.SLASH);
        out.write(pdu.getProxy().getModule().getCode().getBytes(StandardCharsets.ISO_8859_1));
        out.write(LightHttpConstant.SLASH);

        // Connection
        out.write(LightHttpConstant.CONNECTION);
        out.write(LightHttpConstant.SLASH);
        out.write(String.valueOf(pdu.getConnection().getCode()).getBytes(StandardCharsets.ISO_8859_1));
        out.write(LightHttpConstant.SLASH);

        // Target Interceptor Id
        out.write(LightHttpConstant.INTERCEPTOR);
        out.write(LightHttpConstant.SLASH);
        out.write(String.valueOf(targetInterceptorId).getBytes(StandardCharsets.ISO_8859_1));

        // End first line
        out.write(LightHttpConstant.FIRST_LINE_END);
    }

    /**
     * Writes tags as header of the HTTP request to the output stream.
     * <p>Example: T: tag_1,tag_2</p>
     */
    private void writeTagsToOutput(Set<String> tags, OutputStream out) throws IOException {
        if (!tags.isEmpty()) {
            out.write(LightHttpConstant.TAGS_HEADER_BYTES);

            int remaining = tags.size();
            for (String tag : tags) {
                out.write(tag.getBytes(StandardCharsets.ISO_8859_1));

                if (--remaining != 0) {
                    out.write(LightHttpConstant.TAGS_SEPARATOR);
                }
            }
            out.write(LightHttpConstant.HEADER_END);
        }
    }

    /**
     * Writes metadata as headers of the HTTP request to the output stream.
     * <p>Example: M-{metadata.key}: {metadata.value}</p>
     */
    private void writeMetadataToOutput(Map<String, String> serializedMetaData, OutputStream out)
            throws IOException {
        if (serializedMetaData != null) {
            for (var item : serializedMetaData.entrySet()) {
                out.write(LightHttpConstant.METADATA_HEADER_START_BYTES);
                out.write(item.getKey().getBytes(StandardCharsets.ISO_8859_1));
                out.write(LightHttpConstant.HEADER_COLON);
                out.write(item.getValue().getBytes(StandardCharsets.ISO_8859_1));
                out.write(LightHttpConstant.HEADER_END);
            }
        }
    }

}
