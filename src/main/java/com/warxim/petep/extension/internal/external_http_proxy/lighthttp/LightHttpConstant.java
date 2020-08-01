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
package com.warxim.petep.extension.internal.external_http_proxy.lighthttp;

public final class LightHttpConstant {
  /*
   * BYTES
   */
  public static final byte COLON = (byte) 0x3A;
  public static final byte SLASH = (byte) 0x2F;
  public static final byte C2S = (byte) 0x73;
  public static final byte S2C = (byte) 0x63;
  public static final byte CR = (byte) 0x0D;
  public static final byte LF = (byte) 0x0A;
  public static final byte TAGS_SEPARATOR = ',';

  /*
   * STRINGS
   */
  public static final String TAGS_HEADER = "T";
  public static final String CONTENT_TYPE_HEADER = "Content-Type";
  public static final String CONTENT_LENGTH_HEADER = "Content-Length";
  public static final String METADATA_HEADER_START = "M-";

  /*
   * BYTE ARRAYS
   */
  public static final byte[] FIRST_LINE_START = "POST http://".getBytes();
  public static final byte[] FIRST_LINE_END = " HTTP/1.0\r\n".getBytes();
  public static final byte[] CONTENT_LENGTH = (CONTENT_LENGTH_HEADER + ": ").getBytes();
  public static final byte[] CONTENT_TYPE_CHARSET =
      (CONTENT_TYPE_HEADER + ": text/plain; charset=").getBytes();
  public static final byte[] TAGS_HEADER_BYTES = (TAGS_HEADER + ": ").getBytes();
  public static final byte[] METADATA_HEADER_START_BYTES = METADATA_HEADER_START.getBytes();
  public static final byte[] HEADER_COLON = ": ".getBytes();
  public static final byte[] HEADER_END = "\r\n".getBytes();
  public static final byte[] HEADERS_END = "\r\n\r\n".getBytes();

  /*
   * RESPONSES
   */
  public static final byte[] RESPONSE_OK = "HTTP/1.0 OK\r\n\r\n".getBytes();
  public static final byte[] RESPONSE_WRONG_PROXY =
      "HTTP/1.0 404 Proxy not found\r\n\r\n".getBytes();
  public static final byte[] RESPONSE_WRONG_CONNECTION =
      "HTTP/1.0 404 Connection not found\r\n\r\n".getBytes();
  public static final byte[] RESPONSE_WRONG_INTERCEPTOR =
      "HTTP/1.0 404 Interceptor not found\r\n\r\n".getBytes();
  public static final byte[] RESPONSE_DESERIALIZATION_ERROR =
      "HTTP/1.0 500 Deserialization Error\r\n\r\n".getBytes();

  private LightHttpConstant() {}
}
