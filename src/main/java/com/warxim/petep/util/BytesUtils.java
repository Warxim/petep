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

import com.warxim.petep.common.Constant;
import com.warxim.petep.extension.PetepAPI;

import java.nio.charset.Charset;

/**
 * Bytes utils.
 */
@PetepAPI
public final class BytesUtils {
    /**
     * HEX symbols.
     */
    private static final char[] HEX_CHARACTERS = "0123456789ABCDEF".toCharArray();

    private BytesUtils() {
    }

    /**
     * Creates string from given bytes using specified charset.
     * @param buffer Byte buffer containing data
     * @param charset Charset of data in the buffer
     * @return String created from the buffer
     */
    public static String getString(byte[] buffer, Charset charset) {
        return new String(buffer, 0, buffer.length, charset);
    }

    /**
     * Creates string from given bytes using specified charset.
     * @param buffer Byte buffer containing data
     * @param size Size of data inside the buffer
     * @param charset Charset of data in the buffer
     * @return String created from the buffer
     */
    public static String getString(byte[] buffer, int size, Charset charset) {
        return new String(buffer, 0, size, charset);
    }

    /**
     * Gets bytes from given string using specified charset.
     * @param string String from which to get bytes
     * @param charset Charset of the data in the string
     * @return Bytes extracted from the string
     */
    public static byte[] getBytes(String string, Charset charset) {
        return string.getBytes(charset);
    }

    /**
     * Gets bytes from given string using specified charset.
     * @param string String from which to get bytes
     * @param charset Charset of the data in the string
     * @return Bytes extracted from the string
     */
    public static byte[] getBytes(String string, String charset) {
        return string.getBytes(Charset.forName(charset));
    }

    /**
     * Gets bytes from given string using default charset.
     * @param string String from which to get bytes
     * @return Bytes extracted from the string
     */
    public static byte[] getBytes(String string) {
        return string.getBytes(Constant.DEFAULT_CHARSET);
    }

    /**
     * Converts bytes to hex string.
     * @param buffer Byte buffer
     * @param size Size of the data in the buffer
     * @return HEX string representation of the buffer (e.g. 01 23 45)
     */
    public static String bytesToHexString(byte[] buffer, int size) {
        if (size == 0) {
            return "";
        }

        var c = new char[size * 3];
        for (int j = 0; j < size; ++j) {
            int v = buffer[j] & 0xFF;
            c[j * 3] = HEX_CHARACTERS[v >>> 4];
            c[j * 3 + 1] = HEX_CHARACTERS[v & 0x0F];
            c[j * 3 + 2] = ' ';
        }
        return new String(c, 0, c.length - 1);
    }

    /**
     * Converts bytes to hex string.
     * <p>All data from the buffer will be converted to HEX representation.</p>
     * @param buffer Byte buffer
     * @return HEX string representation of the buffer (e.g. 01 23 45)
     */
    public static String bytesToHexString(byte[] buffer) {
        return bytesToHexString(buffer, buffer.length);
    }

    /**
     * Converts hex string (e.g. 01 23 45) to bytes.
     * @param string String to be converted to bytes
     * @return Byte array
     */
    public static byte[] hexStringToBytes(String string) {
        int len = string.length();

        var buffer = new byte[(len + 1) / 3];
        for (int i = 0; i < len; i += 3) {
            buffer[i / 3] =
                    (byte) ((Character.digit(string.charAt(i), 16) << 4) + Character.digit(string.charAt(i + 1), 16));
        }
        return buffer;
    }

    /**
     * Finds first occurrence of array inside of specified buffer, starting at specified offset.
     * @param buffer Byte buffer containing data
     * @param size Size of the data in the buffer
     * @param offset Offset on which to start searching
     * @param what Array of bytes to find
     * @return Returns index of the first occurrence or -1 if not found.
     */
    public static int find(byte[] buffer, int size, int offset, byte[] what) {
        for (int i = offset; i < size; ++i) {
            if (buffer[i] != what[0]) {
                continue;
            }

            if (size - i < what.length) {
                break;
            }

            var found = true;
            for (int j = 1; j < what.length; ++j) {
                if (buffer[i + j] != what[j]) {
                    found = false;
                    break;
                }
            }

            if (found) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Finds specified occurrence of array inside of specified buffer, starting at specified offset.
     * @param buffer Byte buffer containing data
     * @param size Size of the data in the buffer
     * @param offset Offset on which to start searching
     * @param what Array of bytes to find
     * @param n Occurrence to find (zero-indexed)
     * @return Returns index of the specified occurrence or -1 if not found.
     */
    public static int findNth(byte[] buffer, int size, int offset, byte[] what, int n) {
        int occurrence = 0;

        for (int i = offset; i < size; ++i) {
            if (buffer[i] != what[0]) {
                continue;
            }

            if (size - i < what.length) {
                break;
            }

            var found = true;
            for (int j = 1; j < what.length; ++j) {
                if (buffer[i + j] != what[j]) {
                    found = false;
                    break;
                }
            }

            if (found) {
                if (occurrence == n) {
                    return i;
                }
                ++occurrence;
            }
        }

        return -1;
    }

    /**
     * Checks whether the buffer contains specified "what" bytes.
     * @param buffer Byte buffer containing data
     * @param size Size of the data in the buffer
     * @param what Array of bytes to find
     * @return {@code true} if the buffer contains specified data
     */
    public static boolean contains(byte[] buffer, int size, byte[] what) {
        if (what.length == 0) {
            return true;
        }

        for (int i = 0; i < size; ++i) {
            if (buffer[i] != what[0]) {
                continue;
            }

            if (size - i < what.length) {
                break;
            }

            boolean found = true;
            for (int j = 1; j < what.length; ++j) {
                if (buffer[i + j] != what[j]) {
                    found = false;
                    break;
                }
            }

            if (found) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if the buffer contains specified "what" bytes at specified position.
     * @param buffer Byte buffer containing data
     * @param size Size of the data in the buffer
     * @param what Array of bytes to find
     * @param position Expected position of the bytes
     * @return {@code true} if the buffer contains specified data at given position
     */
    public static boolean containsAt(byte[] buffer, int size, byte[] what, int position) {
        if (size < position + what.length) {
            return false;
        }

        for (int i = 0; i < what.length; ++i) {
            if (buffer[position + i] != what[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks whether the data in the buffer ends with given bytes
     * @param buffer Byte buffer containing data
     * @param size Size of the data in the buffer
     * @param what Array of bytes to find
     * @return {@code true} if the buffer ends with "what" bytes
     */
    public static boolean endsWith(byte[] buffer, int size, byte[] what) {
        if (size < what.length) {
            return false;
        }

        int first = size - what.length;
        for (int i = 0; i < what.length; ++i) {
            if (buffer[first + i] != what[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks whether the data in the buffer starts with given bytes
     * @param buffer Byte buffer containing data
     * @param size Size of the data in the buffer
     * @param what Array of bytes to find
     * @return {@code true} if the starts ends with "what" bytes
     */
    public static boolean startsWith(byte[] buffer, int size, byte[] what) {
        if (size < what.length) {
            return false;
        }

        for (int i = 0; i < what.length; ++i) {
            if (buffer[i] != what[i]) {
                return false;
            }
        }

        return true;
    }
}
