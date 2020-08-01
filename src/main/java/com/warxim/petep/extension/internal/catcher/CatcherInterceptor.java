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
package com.warxim.petep.extension.internal.catcher;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.module.InterceptorModule;
import com.warxim.petep.interceptor.worker.Interceptor;

/** Catcher interceptor. */
public final class CatcherInterceptor extends Interceptor {
  private static final int RECHECK_STATE_PERIOD_MS = 25;
  private CatcherController controller;

  /** Catcher interceptor constructor. */
  public CatcherInterceptor(int id, InterceptorModule module, PetepHelper helper) {
    super(id, module, helper);
  }

  @Override
  public boolean prepare() {
    // Get controller.
    controller = ((CatcherExtension) module.getFactory().getExtension()).getController();
    return true;
  }

  @Override
  public boolean intercept(PDU pdu) {
    if (controller == null) {
      // Controller does not exist, let PDU go through.
      return true;
    } else {
      // PDU has no_catch_skip tag and does not have catch tag. (Let it go through the interceptor.)
      if (pdu.hasTag("no_catch_skip") && !pdu.hasTag("catch")) {
        return true;
      }

      // If controller is in transition, wait, so we do not mix PDU order.
      while (controller.getState() == CatcherState.TRANSITION) {
        try {
          Thread.sleep(RECHECK_STATE_PERIOD_MS);
        } catch (InterruptedException e) {
          // Interrupted
          Thread.currentThread().interrupt();
          return false;
        }
      }

      // If catcher is disabled, let PDU go through.
      if (controller.getState() == CatcherState.OFF) {
        return true;
      }

      // Set this interceptor as last PDU interceptor.
      pdu.setLastInterceptor(this);

      // Intercept PDU in GUI.
      controller.catchPdu(pdu);

      // Do not let PDU go to the next interceptor.
      return false;
    }
  }

  @Override
  public void stop() {
    // No action needed.
  }
}
