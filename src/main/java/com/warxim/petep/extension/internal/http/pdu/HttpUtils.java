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
package com.warxim.petep.extension.internal.http.pdu;

/**
 * Utils for HTTP extension
 */
public final class HttpUtils {
    private HttpUtils() {
    }

    /**
     * Formats header name to have standard format.
     * <p>Example: Some-Header-Name</p>
     * @param name Header name to be formatted
     * @return Formatted header name
     */
    public static String formatHeaderName(String name) {
        char[] array = name.toCharArray();

        array[0] = Character.toUpperCase(array[0]);
        int end = array.length - 1;
        for (int i = 1; i < end; ++i) {
            if (array[i] == '-') {
                array[i + 1] = Character.toUpperCase(array[i + 1]);
            }
        }

        return String.valueOf(array);
    }

    /**
     * Formats header name to have standard format.
     * <p>Example: Some-Header-Name</p>
     * @param builder String builder containing header name to be formatted
     * @return Formatted header name
     */
    public static String formatHeaderName(StringBuilder builder) {
        builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));
        int end = builder.length() - 1;
        for (int i = 1; i < end; ++i) {
            if (builder.charAt(i) == '-') {
                builder.setCharAt(i + 1, Character.toUpperCase(builder.charAt(i + 1)));
            }
        }
        return builder.toString();
    }
}
