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

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import com.warxim.petep.common.Constant;
import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.proxy.worker.Proxy;

/** Default PDU implementation. */
@PetepAPI
public class DefaultPdu extends PDU {
  protected byte[] buffer;
  protected int size;
  protected Charset charset;

  /** Default PDU implementation constructor. */
  public DefaultPdu(
      Proxy proxy,
      Connection connection,
      PduDestination destination,
      byte[] buffer,
      int size,
      Set<String> tags) {
    super(proxy, connection, destination, tags);
    this.buffer = buffer;
    this.size = size;
    this.charset = Constant.DEFAULT_CHARSET;
  }

  public DefaultPdu(
      Proxy proxy,
      Connection connection,
      PduDestination destination,
      byte[] buffer,
      int size) {
    super(proxy, connection, destination, new HashSet<>());
    this.buffer = buffer;
    this.size = size;
    this.charset = Constant.DEFAULT_CHARSET;
  }

  /** Returns the buffer. */
  @Override
  public byte[] getBuffer() {
    return buffer;
  }

  /** Returns size of data in the buffer. */
  @Override
  public int getSize() {
    return size;
  }

  /** Sets the buffer and size of data inside the buffer. */
  @Override
  public void setBuffer(byte[] buffer, int size) {
    this.buffer = buffer;
    this.size = size;
  }

  /** Resizes the buffer (expands the buffer when needed, but does not shrink the buffer). */
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
    PDU pdu = new DefaultPdu(proxy, connection, destination, buffer.clone(), size);

    pdu.addTags(tags);

    return pdu;
  }

  @Override
  public Charset getCharset() {
    return charset;
  }

  @Override
  public void setCharset(Charset charset) {
    this.charset = charset;
  }
}
