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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Interceptor worker operates with PDUs through input and output queue by calling subordinate
 * interceptor.
 */
public final class InterceptorWorker implements Runnable {
    /**
     * Interceptor that is called for each PDU in queue
     */
    private final Interceptor interceptor;

    /**
     * Input queue for taking PDUs for intercepting
     */
    private final PduQueue in;

    /**
     * Output queue for sending PDUs to next phase
     */
    private final PduQueue out;

    /**
     * Constructs interceptor worker.
     * @param interceptor Interceptor
     * @param in Queue of ingoing PDUs (for processing)
     * @param out Queue of outgoing PDUs (for next worker to process)
     */
    public InterceptorWorker(Interceptor interceptor, PduQueue in, PduQueue out) {
        this.interceptor = interceptor;
        this.in = in;
        this.out = out;
    }

    /**
     * Runs worker and intercepts PDUs from input queue.
     */
    @Override
    public void run() {
        try {
            PDU pdu;

            // Wait for PDU to appear in the input queue.
            while ((pdu = in.take()) != null) {
                interceptPdu(pdu);
            }
        } catch (InterruptedException e) {
            // Shutdown.
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Intercepts PDU using internal interceptor
     */
    private void interceptPdu(PDU pdu) {
        try {
            // Send the PDU to interceptor and if it returns true, put it into output queue.
            if (interceptor.intercept(pdu)) {
                // Sets last interceptor to current interceptor.
                pdu.setLastInterceptor(interceptor);

                // Puts the PDU to outgoing queue.
                out.add(pdu);
            }
        } catch (RuntimeException e) {
            Logger.getGlobal().log(
                    Level.SEVERE,
                    String.format("Interceptor with code '%s' thrown exception during PDU interception!", interceptor.getCode()),
                    e
            );
        }
    }
}
