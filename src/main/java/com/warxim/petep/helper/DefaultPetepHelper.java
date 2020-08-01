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
package com.warxim.petep.helper;

import java.util.List;
import com.warxim.petep.core.PETEP;
import com.warxim.petep.core.PetepState;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.interceptor.worker.Interceptor;
import com.warxim.petep.proxy.worker.Proxy;

/** Default implementation of PETEP helper. */
public final class DefaultPetepHelper implements PetepHelper {
  private final PETEP petep;

  /** Constructor of default PETEP helper implementation. */
  public DefaultPetepHelper(PETEP petep) {
    this.petep = petep;
  }

  @Override
  public PetepState getState() {
    return petep.getState();
  }

  @Override
  public void processPdu(PDU pdu) {
    if (pdu.getDestination() == PduDestination.SERVER) {
      petep.processC2S(pdu);
    } else {
      petep.processS2C(pdu);
    }
  }

  @Override
  public void processPdu(PDU pdu, int interceptorId) {
    if (pdu.getDestination() == PduDestination.SERVER) {
      petep.processC2S(pdu, interceptorId);
    } else {
      petep.processS2C(pdu, interceptorId);
    }
  }

  @Override
  public void sendPdu(PDU pdu) {
    if (pdu.getDestination() == PduDestination.SERVER) {
      petep.sendC2S(pdu);
    } else {
      petep.sendS2C(pdu);
    }
  }

  @Override
  public List<Proxy> getProxies() {
    return petep.getProxyManager().getList();
  }

  @Override
  public Proxy getProxy(String code) {
    return petep.getProxyManager().get(code);
  }

  @Override
  public List<Interceptor> getInterceptorsC2S() {
    return petep.getInterceptorManagerC2S().getList();
  }

  @Override
  public List<Interceptor> getInterceptorsS2C() {
    return petep.getInterceptorManagerS2C().getList();
  }
}
