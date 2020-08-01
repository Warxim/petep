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
package com.warxim.petep.core;

import com.warxim.petep.core.listener.PetepListener;
import com.warxim.petep.core.listener.PetepListenerManager;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.module.InterceptorModuleContainer;
import com.warxim.petep.proxy.module.ProxyModuleContainer;

/**
 * PETEP manager allows application to start & stop PETEP and hands over the resources to the PETEP
 * core.
 */
public final class PetepManager implements PetepListener {
  private final ProxyModuleContainer proxyModuleContainer;
  private final InterceptorModuleContainer interceptorModuleContainerC2S;
  private final InterceptorModuleContainer interceptorModuleContainerS2C;

  private final PetepListenerManager petepListenerManager;

  private PETEP petep;

  public PetepManager(
      ProxyModuleContainer proxyModuleManager,
      InterceptorModuleContainer interceptorModuleContainerC2S,
      InterceptorModuleContainer interceptorModuleContainerS2C,
      PetepListenerManager petepListenerManager) {
    this.proxyModuleContainer = proxyModuleManager;
    this.interceptorModuleContainerC2S = interceptorModuleContainerC2S;
    this.interceptorModuleContainerS2C = interceptorModuleContainerS2C;
    this.petepListenerManager = petepListenerManager;

    petep = null;

    petepListenerManager.registerListener(this);
  }

  /*
   * CONTROL
   */
  /** Creates and starts PETEP. */
  public synchronized void start() {
    if (petep != null) {
      return;
    }

    petep = new PETEP(proxyModuleContainer, interceptorModuleContainerC2S,
        interceptorModuleContainerS2C, petepListenerManager);

    petep.start();
  }

  /** Stops PETEP. */
  public synchronized void stop() {
    if (petep == null) {
      return;
    }

    petep.stop();
  }

  /*
   * GETTERS
   */
  /** Returns PETEP state. */
  public synchronized PetepState getState() {
    if (petep == null) {
      return PetepState.STOPPED;
    }

    return petep.getState();
  }

  public ProxyModuleContainer getProxyModuleContainer() {
    return proxyModuleContainer;
  }

  /** Returns interceptor module container for direction C2S. (Client -> Server) */
  public InterceptorModuleContainer getInterceptorModuleContainerC2S() {
    return interceptorModuleContainerC2S;
  }

  /** Returns interceptor module container for direction S2C. (Client <- Server) */
  public InterceptorModuleContainer getInterceptorModuleContainerS2C() {
    return interceptorModuleContainerS2C;
  }

  public PetepListenerManager getPetepListenerManager() {
    return petepListenerManager;
  }

  /*
   * LISTENER
   */
  @Override
  public void beforePrepare(PetepHelper helper) {
    // No action needed.
  }

  @Override
  public void afterPrepare(PetepHelper helper) {
    // No action needed.
  }

  @Override
  public void afterStart(PetepHelper helper) {
    // No action needed.
  }

  @Override
  public void beforeStart(PetepHelper helper) {
    // No action needed.
  }

  @Override
  public void beforeStop(PetepHelper helper) {
    // No action needed.
  }

  @Override
  public synchronized void afterStop(PetepHelper helper) {
    petep = null;
  }
}
