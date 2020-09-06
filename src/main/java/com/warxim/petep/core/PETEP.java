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

import java.util.logging.Logger;
import com.warxim.petep.core.listener.PetepListener;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.helper.DefaultPetepHelper;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.interceptor.module.InterceptorModuleContainer;
import com.warxim.petep.interceptor.worker.InterceptorExecutor;
import com.warxim.petep.interceptor.worker.InterceptorManager;
import com.warxim.petep.proxy.module.ProxyModuleContainer;
import com.warxim.petep.proxy.worker.ProxyExecutor;
import com.warxim.petep.proxy.worker.ProxyManager;

/** PETEP core class. */
public final class PETEP {
  private final PetepListener listener;
  private final PetepHelper helper;
  private final Thread thread;

  private PetepState state;

  // Managers
  private final ProxyManager proxyManager;
  private final InterceptorManager interceptorManagerC2S;
  private final InterceptorManager interceptorManagerS2C;

  // Executors
  private final ProxyExecutor proxyExecutor;
  private final InterceptorExecutor interceptorExecutorC2S;
  private final InterceptorExecutor interceptorExecutorS2C;

  /** PETEP constructor. */
  public PETEP(
      ProxyModuleContainer proxyModuleContainer,
      InterceptorModuleContainer interceptorModuleContainerC2S,
      InterceptorModuleContainer interceptorModuleContainerS2C,
      PetepListener listener) {
    this.listener = listener;

    // Create helper.
    helper = new DefaultPetepHelper(this);

    // Create managers.
    proxyManager = new ProxyManager(helper, proxyModuleContainer);
    interceptorManagerC2S = new InterceptorManager(helper, interceptorModuleContainerC2S);
    interceptorManagerS2C = new InterceptorManager(helper, interceptorModuleContainerS2C);

    // Create executors.
    proxyExecutor = new ProxyExecutor(proxyManager);

    interceptorExecutorC2S = new InterceptorExecutor(interceptorManagerC2S, this::sendC2S);
    interceptorExecutorS2C = new InterceptorExecutor(interceptorManagerS2C, this::sendS2C);

    state = PetepState.STOPPED;

    this.thread = new Thread(this::run);
  }

  /** Starts PETEP. */
  public void start() {
    if (state != PetepState.STOPPED) {
      return;
    }

    thread.start();
  }

  /** Stops PETEP. */
  public void stop() {
    if (state != PetepState.STARTED) {
      return;
    }

    thread.interrupt();
  }

  /** Runs PETEP. */
  private void run() {
    listener.beforePrepare(helper);
    if (!runPrepare()) {
      Logger.getGlobal().severe("PETEP failed to prepare!");
    } else {
      listener.afterPrepare(helper);

      listener.beforeStart(helper);
      if (!runStart()) {
        Logger.getGlobal().severe("PETEP failed to start!");
      } else {
        listener.afterStart(helper);

        try {
          thread.join();
        } catch (InterruptedException e) {
          Logger.getGlobal().info("PETEP interrupted...");
          Thread.currentThread().interrupt();
        }
      }
    }

    listener.beforeStop(helper);
    runStop();
    listener.afterStop(helper);
  }

  /** Runs prepare. */
  private boolean runPrepare() {
    Logger.getGlobal().info("PETEP preparing...");
    state = PetepState.PREPARING;

    if (!proxyExecutor.prepare() || !interceptorExecutorC2S.prepare() || !interceptorExecutorS2C.prepare()) {
      return false;
    }

    state = PetepState.PREPARED;
    Logger.getGlobal().info("PETEP prepared...");

    return true;
  }

  /** Runs start. */
  private boolean runStart() {
    Logger.getGlobal().info("PETEP starting...");
    state = PetepState.STARTING;

    if (!proxyExecutor.start()) {
      return false;
    }

    interceptorExecutorC2S.start();
    interceptorExecutorS2C.start();

    state = PetepState.STARTED;
    Logger.getGlobal().info("PETEP started...");

    return true;
  }

  /** Runs stop. */
  private void runStop() {
    Logger.getGlobal().info("PETEP stopping...");
    state = PetepState.STOPPING;

    proxyExecutor.stop();
    interceptorExecutorC2S.stop();
    interceptorExecutorS2C.stop();

    state = PetepState.STOPPED;
    Logger.getGlobal().info("PETEP stopped...");
  }

  /** Sends PDU in direction C2S out of PETEP. (Client -> Server) */
  public void sendC2S(PDU pdu) {
    pdu.getConnection().sendC2S(pdu);
  }

  /** Sends PDU in direction S2C out of PETEP. (Client <- Server) */
  public void sendS2C(PDU pdu) {
    pdu.getConnection().sendS2C(pdu);
  }

  /** Processes PDU internally in direction C2S. (Client -> Server) */
  public void processC2S(PDU pdu) {
    if (pdu.getLastInterceptor() == null) {
      interceptorExecutorC2S.intercept(pdu);
    } else {
      interceptorExecutorC2S.intercept(pdu, pdu.getLastInterceptor().getId() + 1);
    }
  }

  /** Processes PDU internally in direction S2C. (Client <- Server) */
  public void processS2C(PDU pdu) {
    if (pdu.getLastInterceptor() == null) {
      interceptorExecutorS2C.intercept(pdu);
    } else {
      interceptorExecutorS2C.intercept(pdu, pdu.getLastInterceptor().getId() + 1);
    }
  }

  /**
   * Processes PDU internally in direction C2S and start in specified interceptor. (Client -> Server)
   */
  public void processC2S(PDU pdu, int interceptorId) {
    interceptorExecutorC2S.intercept(pdu, interceptorId);
  }

  /**
   * Processes PDU internally in direction S2C and start in specified interceptor. (Client <- Server)
   */
  public void processS2C(PDU pdu, int interceptorId) {
    interceptorExecutorS2C.intercept(pdu, interceptorId);
  }

  public PetepState getState() {
    return state;
  }

  public ProxyManager getProxyManager() {
    return proxyManager;
  }

  /** Returns interceptor manager in direction C2S. (Client -> Server) */
  public InterceptorManager getInterceptorManagerC2S() {
    return interceptorManagerC2S;
  }

  /** Returns interceptor manager in direction S2C. (Client <- Server) */
  public InterceptorManager getInterceptorManagerS2C() {
    return interceptorManagerS2C;
  }
}
