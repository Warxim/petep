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

import java.util.Set;
import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.tcp.proxy.base.TcpPdu;
import com.warxim.petep.proxy.worker.Proxy;

public final class WebSocketPdu extends TcpPdu {
  private boolean isFinal;

  private boolean isRsv1;
  private boolean isRsv2;
  private boolean isRsv3;

  private Opcode opcode;

  private boolean isMasked;
  private byte[] mask;

  public WebSocketPdu(
      Proxy proxy,
      Connection connection,
      PduDestination destination,
      byte[] buffer,
      int size) {
    super(proxy, connection, destination, buffer, size);
  }

  public WebSocketPdu(
      Proxy proxy,
      Connection connection,
      PduDestination destination,
      byte[] buffer,
      int size,
      Set<String> tags) {
    super(proxy, connection, destination, buffer, size, tags);
  }

  public boolean isFinal() {
    return isFinal;
  }

  public void setFinal(boolean isFinal) {
    this.isFinal = isFinal;
  }

  public Opcode getOpcode() {
    return opcode;
  }

  public void setOpcode(Opcode opcode) {
    this.opcode = opcode;
  }

  public boolean isRsv1() {
    return isRsv1;
  }

  public void setRsv1(boolean isRsv1) {
    this.isRsv1 = isRsv1;
  }

  public boolean isRsv2() {
    return isRsv2;
  }

  public void setRsv2(boolean isRsv2) {
    this.isRsv2 = isRsv2;
  }

  public boolean isRsv3() {
    return isRsv3;
  }

  public void setRsv3(boolean isRsv3) {
    this.isRsv3 = isRsv3;
  }

  public boolean isMasked() {
    return isMasked;
  }

  public void setMasked(boolean isMasked) {
    this.isMasked = isMasked;
  }

  public byte[] getMask() {
    return mask;
  }

  public void setMask(byte[] mask) {
    this.mask = mask;
  }

  @Override
  public WebSocketPdu copy() {
    WebSocketPdu pdu = new WebSocketPdu(proxy, connection, destination, buffer.clone(), size);

    pdu.addTags(tags);

    pdu.setFinal(isFinal);

    pdu.setRsv1(isRsv1);
    pdu.setRsv2(isRsv2);
    pdu.setRsv3(isRsv3);

    pdu.setOpcode(opcode);

    pdu.setMasked(isMasked);
    if (isMasked) {
      pdu.setMask(mask.clone());
    }

    return pdu;
  }
}
