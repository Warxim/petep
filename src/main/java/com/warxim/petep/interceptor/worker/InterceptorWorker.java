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
package com.warxim.petep.interceptor.worker;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduQueue;

/**
 * Interceptor worker operates with PDUs through input and output queue by calling subordinate
 * interceptor.
 */
public final class InterceptorWorker implements Runnable {
  private final Interceptor interceptor;

  private final PduQueue in;
  private final PduQueue out;

  public InterceptorWorker(Interceptor interceptor, PduQueue in, PduQueue out) {
    this.interceptor = interceptor;
    this.in = in;
    this.out = out;
  }

  /** Runs worker. */
  @Override
  public void run() {
    try {
      PDU pdu;

      // Wait for PDU to appear in the input queue.
      while ((pdu = in.take()) != null) {
        // Send the PDU to interceptor and if it returns true, put it into output queue.
        if (interceptor.intercept(pdu)) {
          // Sets last interceptor to current interceptor.
          pdu.setLastInterceptor(interceptor);

          // Puts the PDU to outgoing queue.
          out.add(pdu);
        }
      }
    } catch (InterruptedException e) {
      // Shutdown.
      Thread.currentThread().interrupt();
    }
  }
}
