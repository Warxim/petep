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
package com.warxim.petep.core.connection;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.core.pdu.PduQueue;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.proxy.worker.Proxy;

/**
 * Connection in PETEP proxy has to handle both connection between client and proxy and between
 * proxy and server. Contains two outgoing queues that contain PDUs that should be sent in a given
 * direction (C2S / S2C).
 */
@PetepAPI
public abstract class Connection {
  /** Unique nummeric identifier of the connection. */
  protected final int id;

  /** Parent proxy. */
  protected final Proxy proxy;

  /** Outgoing queue in direction C2S (client -> server). */
  protected final PduQueue queueC2S;

  /** Outgoing queue in direction S2C (client <- server). */
  protected final PduQueue queueS2C;

  /** Connection constructor. */
  public Connection(int id, Proxy proxy) {
    this.id = id;
    this.proxy = proxy;
    this.queueC2S = new PduQueue();
    this.queueS2C = new PduQueue();
  }

  public int getId() {
    return id;
  }

  /** Sends PDU outside the PETEP. */
  public final void send(PDU pdu) {
    if (pdu.getDestination() == PduDestination.SERVER) {
      queueC2S.add(pdu);
    } else {
      queueS2C.add(pdu);
    }
  }

  /** Sends PDU outside the PETEP in direction C2S (client -> server). */
  public final void sendC2S(PDU pdu) {
    queueC2S.add(pdu);
  }

  /** Sends PDU outside the PETEP in direction S2C (client <- server). */
  public final void sendS2C(PDU pdu) {
    queueS2C.add(pdu);
  }

  /** Processes PDU in PETEP core. */
  protected final void process(PDU pdu) {
    proxy.getHelper().processPdu(pdu);
  }

  /** About connection. */
  @Override
  public String toString() {
    return "Connection " + id;
  }

  /**
   * Starts connection.
   *
   * <p>
   * @return Returns true if the start was successful.
   *
   * <p>
   * <b>Attention:</b> this method should return ASAP - it should be used to create threads and then
   * return immediately.
   */
  public abstract boolean start();

  /** Stops connection. */
  public abstract void stop();
}
