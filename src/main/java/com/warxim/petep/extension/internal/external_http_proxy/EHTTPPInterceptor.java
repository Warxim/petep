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
package com.warxim.petep.extension.internal.external_http_proxy;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduQueue;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.worker.Interceptor;

/** External HTTP Proxy interceptor. */
public final class EHTTPPInterceptor extends Interceptor {
  private PduQueue queue;

  /** External HTTP Proxy interceptor constructor. */
  public EHTTPPInterceptor(int id, EHTTPPInterceptorModule module, PetepHelper helper) {
    super(id, module, helper);
    queue = null;
  }

  @Override
  public boolean prepare() {
    return true;
  }

  @Override
  public boolean intercept(PDU pdu) {
    if (queue != null && (pdu.hasTag("httpp") || !pdu.hasTag("no_httpp"))) {
      queue.add(pdu);
      return false;
    }
    return true;
  }

  @Override
  public void stop() {
    // No action needed.
  }

  public void setQueue(PduQueue queue) {
    this.queue = queue;
  }
}
