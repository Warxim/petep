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

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.PetepAPI;

/** PDU utils. */
@PetepAPI
public final class PduUtils {
  private PduUtils() {}

  /** Converts PDU buffer to string. */
  public static String bufferToString(PDU pdu) {
    return BytesUtils.bytesToString(pdu.getBuffer(), pdu.getSize());
  }

  /** Converts tags to string. */
  public static String tagsToString(Set<String> tags) {
    if (tags.isEmpty()) {
      return "";
    }

    StringJoiner joiner = new StringJoiner(",");
    for (String tag : tags) {
      joiner.add(tag);
    }

    return joiner.toString();
  }

  /** Converts string to tags. */
  public static Set<String> stringToTags(String str) {
    Set<String> tags = new HashSet<>();
    StringBuilder builder = new StringBuilder();

    char[] arr = str.toCharArray();
    for (char c : arr) {
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

  /** Replaces "what" bytes with "with" bytes for specified occurrence. */
  public static void replace(PDU pdu, byte[] what, byte[] with, int occurrence) {
    byte[] buffer = pdu.getBuffer();
    int size = pdu.getSize();

    if (what.length == with.length) {
      int position = BytesUtils.findNth(buffer, size, 0, what, occurrence);

      // Not found? Return.
      if (position == -1) {
        return;
      }

      // Replace specific number of bytes with "with".
      System.arraycopy(with, 0, buffer, position, with.length);

      pdu.setBuffer(buffer, size);
    } else {
      int position = BytesUtils.findNth(buffer, size, 0, what, occurrence);

      if (position == -1) {
        return;
      }

      // Create new buffer if needed or use the old buffer.
      byte[] newBuffer;

      int oldSize = size;

      size = size + (with.length - what.length);

      if (buffer.length < size) {
        // Double the buffer or resize to size (prefers doubling the size).
        if (buffer.length * 2 > size) {
          newBuffer = new byte[buffer.length * 2];
        } else {
          newBuffer = new byte[size];
        }
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
  }

  /** Replaces all "what" bytes with "with" bytes. */
  public static void replace(PDU pdu, byte[] what, byte[] with) {
    byte[] buffer = pdu.getBuffer();
    int size = pdu.getSize();
    int offset = 0;

    if (what.length == with.length) {
      while (true) {
        int position = BytesUtils.find(buffer, size, offset, what);

        if (position == -1) {
          break;
        }

        offset = position + with.length;

        System.arraycopy(with, 0, buffer, position, with.length);
      }

      pdu.setBuffer(buffer, size);
    } else {
      while (true) {
        int position = BytesUtils.find(buffer, size, offset, what);

        if (position == -1) {
          break;
        }

        // Move offset.
        offset = position + with.length;

        byte[] newBuffer;

        int oldSize = size;

        // Create new buffer if needed.
        size = size + (with.length - what.length);

        if (buffer.length < size) {
          // Double the buffer or resize to size (prefers doubling the size).
          if (buffer.length * 2 > size) {
            newBuffer = new byte[buffer.length * 2];
          } else {
            newBuffer = new byte[size];
          }
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

  /**
   * Finds first occurrence of array inside of specified buffer, starting at specified offset.
   *
   * @return Returns index of the first occurrence or -1 if not found.
   */
  public static int find(PDU pdu, int offset, byte[] array) {
    return BytesUtils.find(pdu.getBuffer(), pdu.getSize(), offset, array);
  }

  /**
   * Finds specified occurrence of array inside of specified buffer, starting at specified offset.
   *
   * @return Returns index of the specified occurrence or -1 if not found.
   */
  public static int findNth(PDU pdu, int offset, byte[] array, int n) {
    return BytesUtils.findNth(pdu.getBuffer(), pdu.getSize(), offset, array, n);
  }

  /** Returns true if the buffer contains specified "what" bytes. */
  public static boolean contains(PDU pdu, byte[] what) {
    return BytesUtils.contains(pdu.getBuffer(), pdu.getSize(), what);
  }

  /** Returns true if the buffer contains specified "what" bytes at specified position. */
  public static boolean containsAt(PDU pdu, byte[] what, int position) {
    return BytesUtils.containsAt(pdu.getBuffer(), pdu.getSize(), what, position);
  }

  /** Returns true if the buffer ends with "what" bytes. */
  public static boolean endsWith(PDU pdu, byte[] what) {
    return BytesUtils.endsWith(pdu.getBuffer(), pdu.getSize(), what);
  }

  /** Returns true if the buffer starts with "what" bytes. */
  public static boolean startsWith(PDU pdu, byte[] what) {
    return BytesUtils.startsWith(pdu.getBuffer(), pdu.getSize(), what);
  }
}
