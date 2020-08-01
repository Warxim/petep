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

import com.warxim.petep.extension.PetepAPI;

/** Bytes utils. */
@PetepAPI
public final class BytesUtils {
  /** HEX symbols. */
  private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

  private BytesUtils() {}

  /** Converts bytes to string. */
  public static String bytesToString(byte[] buffer, int size) {
    if (size == 0) {
      return "";
    }

    char[] c = new char[size * 3];
    for (int j = 0; j < size; ++j) {
      int v = buffer[j] & 0xFF;
      c[j * 3] = hexArray[v >>> 4];
      c[j * 3 + 1] = hexArray[v & 0x0F];
      c[j * 3 + 2] = ' ';
    }
    return new String(c, 0, c.length - 1);
  }

  /** Converts bytes to string. */
  public static String bytesToString(byte[] buffer) {
    return bytesToString(buffer, buffer.length);
  }

  /** Converts string to bytes. */
  public static byte[] stringToBytes(String s) {
    int len = s.length();

    byte[] buffer = new byte[(len + 1) / 3];
    for (int i = 0; i < len; i += 3) {
      buffer[i / 3] =
          (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
    }
    return buffer;
  }

  /**
   * Finds first occurrence of array inside of specified buffer, starting at specified offset.
   *
   * @return Returns index of the first occurrence or -1 if not found.
   */
  public static int find(byte[] buffer, int size, int offset, byte[] array) {
    for (int i = offset; i < size; ++i) {
      if (buffer[i] != array[0]) {
        continue;
      }

      if (size - i < array.length) {
        break;
      }

      boolean found = true;
      for (int j = 1; j < array.length; ++j) {
        if (buffer[i + j] != array[j]) {
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
   *
   * @return Returns index of the specified occurrence or -1 if not found.
   */
  public static int findNth(byte[] buffer, int size, int offset, byte[] array, int n) {
    int occurrence = 0;

    for (int i = offset; i < size; ++i) {
      if (buffer[i] != array[0]) {
        continue;
      }

      if (size - i < array.length) {
        break;
      }

      boolean found = true;
      for (int j = 1; j < array.length; ++j) {
        if (buffer[i + j] != array[j]) {
          found = false;
          break;
        }
      }

      if (found) {
        if (occurrence == n) {
          return i;
        } else {
          ++occurrence;
        }
      }
    }

    return -1;
  }

  /** Returns true if the buffer contains specified "what" bytes. */
  public static boolean contains(byte[] buffer, int size, byte[] what) {
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

  /** Returns true if the buffer contains specified "what" bytes at specified position. */
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

  /** Returns true if the buffer ends with "what" bytes. */
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

  /** Returns true if the buffer starts with "what" bytes. */
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
