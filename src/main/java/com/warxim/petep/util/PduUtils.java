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
package com.warxim.petep.util;

import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.core.pdu.SerializedPdu;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.helper.PetepHelper;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

/**
 * PDU utils.
 */
@PetepAPI
public final class PduUtils {
    private PduUtils() {
    }

    /**
    * Convert PDU buffer to string.
     * @param pdu PDU to convert
     * @return String content of the PDU
    */
    public static String getString(PDU pdu) {
        return BytesUtils.getString(pdu.getBuffer(), pdu.getSize(), pdu.getCharset());
    }

    /**
     * Converts PDU buffer to hex string (e.g. 77 61 72 78 69 6D 2E 63 6F 6D).
     * @param pdu PDU to convert
     * @return Hex string (e.g. 77 61 72 78 69 6D 2E 63 6F 6D)
     */
    public static String bufferToHexString(PDU pdu) {
        return BytesUtils.bytesToHexString(pdu.getBuffer(), pdu.getSize());
    }

    /**
     * Converts tags to string (e.g. tag_1,tag_2).
     * @param tags Tags to be converted to string
     * @return Tags joined to string (e.g. tag_1,tag_2)
     */
    public static String tagsToString(Set<String> tags) {
        if (tags.isEmpty()) {
            return "";
        }

        var joiner = new StringJoiner(",");
        for (var tag : tags) {
            joiner.add(tag);
        }

        return joiner.toString();
    }

    /**
     * Converts string (e.g. tag_1,tag_2) to tags.
     * @param string String containing serialized tags (e.g. tag_1,tag_2)
     * @return Set of tags
     */
    public static Set<String> stringToTags(String string) {
        var tags = new HashSet<String>();
        var builder = new StringBuilder();

        var arr = string.toCharArray();
        for (var c : arr) {
            if (c == ',') {
                if (builder.length() > 0) {
                    tags.add(builder.toString());
                }
                builder.setLength(0);
            } else {
                builder.append(c);
            }
        }

        if (builder.length() > 0) {
            tags.add(builder.toString());
        }
        return tags;
    }

    /**
     * Replaces "what" bytes with "with" bytes for specified occurrence (zero-indexed).
     * @param pdu PDU in which to replace data
     * @param what What bytes to replace
     * @param with With what to replace the bytes
     * @param occurrence Which occurrence to replace (zero-indexed)
     */
    public static void replace(PDU pdu, byte[] what, byte[] with, int occurrence) {
        var buffer = pdu.getBuffer();
        var size = pdu.getSize();

        int position = BytesUtils.findNth(buffer, size, 0, what, occurrence);
        if (position == -1) {
            return; // Not found? Return.
        }

        // Replace in case that the lengths are equeal
        if (what.length == with.length) {
            // Replace specific number of bytes with "with".
            System.arraycopy(with, 0, buffer, position, with.length);

            pdu.setBuffer(buffer, size);
            return;
        }

        // Create new buffer if needed or use the old buffer.
        byte[] newBuffer;
        int oldSize = size;
        size = size + (with.length - what.length);

        if (buffer.length < size) {
            // Double the buffer or resize to size (prefers doubling the size).
            newBuffer = buffer.length * 2 > size
                    ? new byte[buffer.length * 2]
                    : new byte[size];
        } else {
            newBuffer = new byte[buffer.length];
        }

        // Add preceding bytes to the buffer.
        System.arraycopy(buffer, 0, newBuffer, 0, position);
        // Add "with" to the buffer.
        System.arraycopy(with, 0, newBuffer, position, with.length);

        // Add following bytes to the buffer.
        int oldIndex = position + what.length;
        System.arraycopy(buffer, oldIndex, newBuffer, position + with.length, oldSize - oldIndex);

        pdu.setBuffer(newBuffer, size);
    }

    /**
     * Replaces all "what" bytes with "with" bytes.
     * @param pdu PDU in which to replace data
     * @param what What bytes to replace
     * @param with With what to replace the bytes
     */
    public static void replace(PDU pdu, byte[] what, byte[] with) {
        if (what.length == with.length) {
            replaceWithSameSize(pdu, what, with);
            return;
        }

        replaceWithDifferentSize(pdu, what, with);
    }

    /**
     * Finds first occurrence of array inside of specified buffer, starting at specified offset.
     * @param pdu PDU in which to find specified data bytes
     * @param offset Offset on which to start searching
     * @param what What bytes to find
     * @return Returns index of the first occurrence or -1 if not found.
     */
    public static int find(PDU pdu, int offset, byte[] what) {
        return BytesUtils.find(pdu.getBuffer(), pdu.getSize(), offset, what);
    }

    /**
     * Finds specified occurrence of array inside of specified buffer, starting at specified offset.
     * @param pdu PDU in which to find specified data bytes
     * @param offset Offset on which to start searching
     * @param what What bytes to find
     * @param n Occurrence to find (zero-indexed)
     * @return Returns index of the specified occurrence or -1 if not found.
     */
    public static int findNth(PDU pdu, int offset, byte[] what, int n) {
        return BytesUtils.findNth(pdu.getBuffer(), pdu.getSize(), offset, what, n);
    }

    /**
     * Checks whether the PDU buffer contains specified "what" bytes.
     * @param pdu PDU in which to find specified data bytes
     * @param what Array of bytes to find
     * @return {@code true} if the PDU buffer contains specified data
     */
    public static boolean contains(PDU pdu, byte[] what) {
        return BytesUtils.contains(pdu.getBuffer(), pdu.getSize(), what);
    }

    /**
     * Returns true if the buffer contains specified "what" bytes at specified position.
     * @param pdu PDU in which to find specified data bytes
     * @param what Array of bytes to find
     * @param position Expected position of the bytes
     * @return {@code true} if the PDU buffer contains specified data at given position
     */
    public static boolean containsAt(PDU pdu, byte[] what, int position) {
        return BytesUtils.containsAt(pdu.getBuffer(), pdu.getSize(), what, position);
    }

    /**
     * Checks whether the PDU buffer ends with "what" bytes.
     * @param pdu PDU in which to find specified data bytes
     * @param what Array of bytes to find
     * @return {@code true} if the PDU buffer ends with "what" bytes
     */
    public static boolean endsWith(PDU pdu, byte[] what) {
        return BytesUtils.endsWith(pdu.getBuffer(), pdu.getSize(), what);
    }

    /**
     * Checks whether the PDU buffer starts with "what" bytes.
     * @param pdu PDU in which to find specified data bytes
     * @param what Array of bytes to find
     * @return {@code true} if the PDU buffer starts with "what" bytes
     */
    public static boolean startsWith(PDU pdu, byte[] what) {
        return BytesUtils.startsWith(pdu.getBuffer(), pdu.getSize(), what);
    }

    /**
     * Convert PDU buffer to serialized representation.
     * @param pdu PDU to be converted
     * @return Serialized PDU
     */
    public static SerializedPdu serializePdu(PDU pdu) {
        if (pdu == null) {
            return null;
        }

        var builder = SerializedPdu.builder();

        var proxy = pdu.getProxy();
        if (proxy != null) {
            builder.proxy(proxy.getModule().getCode());
            var metadata = proxy.getModule().getFactory().getSerializer()
                    .serializePduMetadata(pdu);
            builder.metadata(metadata);
        }

        if (pdu.getConnection() != null) {
            builder.connection(pdu.getConnection().getCode());
        }

        builder.buffer(pdu.getBuffer().clone());
        builder.destination(pdu.getDestination());

        if (pdu.getLastInterceptor() != null) {
            builder.interceptor(pdu.getLastInterceptor().getModule().getCode());
        }

        builder.charset(pdu.getCharset());
        builder.tags(pdu.getTags());

        if (pdu.getProxy() != null) {
            builder.proxy(pdu.getProxy().getModule().getCode());
        }

        return builder.build();
    }


    /**
     * Deserializes the serialized PDU to PDU object
     * @param serializedPdu PDU to be deserialized
     * @param petepHelper Helper for accessing currently running core
     * @return  Deserialized PDU if it was possible to deserialize it;<br>
     *          Returns empty optional if the PDU could not be deserialized
     */
    public static Optional<PDU> deserializePdu(SerializedPdu serializedPdu, PetepHelper petepHelper) {
        var maybeProxy = petepHelper.getProxy(serializedPdu.getProxy());
        if (maybeProxy.isEmpty()) {
            return Optional.empty();
        }
        var proxy = maybeProxy.get();

        Connection connection;
        if (serializedPdu.getConnection() != null) {
            connection = proxy.getConnectionManager()
                    .get(serializedPdu.getConnection())
                    .orElse(null);
        } else {
            connection = null;
        }

        var maybePdu = proxy.getModule().getFactory().getDeserializer()
                .deserializePdu(
                        proxy,
                        connection,
                        serializedPdu.getDestination(),
                        serializedPdu.getBuffer(),
                        serializedPdu.getBuffer().length,
                        serializedPdu.getCharset(),
                        serializedPdu.getTags(),
                        serializedPdu.getMetadata()
                );
        if (maybePdu.isEmpty()) {
            return Optional.empty();
        }
        var pdu = maybePdu.get();

        var interceptors = pdu.getDestination() == PduDestination.SERVER
                ? petepHelper.getInterceptorsC2S()
                : petepHelper.getInterceptorsS2C();

        var lastInterceptor = interceptors
                .stream().filter(interceptor -> interceptor.getModule().getCode().equals(serializedPdu.getInterceptor()))
                .findAny();

        if (lastInterceptor.isPresent()) {
            pdu.setLastInterceptor(lastInterceptor.get());
        }

        return Optional.of(pdu);
    }

    /**
     * Replaces all "what" bytes with "with" bytes, when "what" is the same length as "with".
     */
    private static void replaceWithSameSize(PDU pdu, byte[] what, byte[] with) {
        var buffer = pdu.getBuffer();
        var size = pdu.getSize();
        var offset = 0;
        while (true) {
            int position = BytesUtils.find(buffer, size, offset, what);
            if (position == -1) {
                break; // Not found? Return.
            }

            offset = position + with.length;
            System.arraycopy(with, 0, buffer, position, with.length);
        }
        pdu.setBuffer(buffer, size);
    }

    /**
     * Replaces all "what" bytes with "with" bytes, when "what" is different length than "with".
     */
    private static void replaceWithDifferentSize(PDU pdu, byte[] what, byte[] with) {
        var buffer = pdu.getBuffer();
        var size = pdu.getSize();
        var offset = 0;

        while (true) {
            int position = BytesUtils.find(buffer, size, offset, what);
            if (position == -1) {
                break; // Not found? Return.
            }

            // Move offset.
            offset = position + with.length;
            byte[] newBuffer;
            int oldSize = size;
            // Create new buffer if needed.
            size = size + (with.length - what.length);

            if (buffer.length < size) {
                // Double the buffer or resize to size (prefers doubling the size).
                newBuffer = buffer.length * 2 > size
                        ? new byte[buffer.length * 2]
                        : new byte[size];
            } else {
                newBuffer = new byte[buffer.length];
            }

            // Add preceding bytes to the buffer.
            System.arraycopy(buffer, 0, newBuffer, 0, position);
            // Add "with" to the buffer.
            System.arraycopy(with, 0, newBuffer, position, with.length);

            // Add following bytes to the buffer.
            int oldIndex = position + what.length;
            System.arraycopy(buffer, oldIndex, newBuffer, position + with.length, oldSize - oldIndex);

            // Set buffer to the new buffer.
            buffer = newBuffer;
        }

        pdu.setBuffer(buffer, size);
    }
}
