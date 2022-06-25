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

import com.warxim.petep.core.listener.ConnectionListenerManager;
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

import java.util.logging.Logger;

/**
 * PETEP core class.
 * <p>Represents PETEP core, which can be started/stopped.</p>
 */
public final class PETEP {
    private final PetepListener petepListener;
    private final ConnectionListenerManager connectionListenerManager;
    private final PetepHelper helper;

    // Managers
    private final ProxyManager proxyManager;
    private final InterceptorManager interceptorManagerC2S;
    private final InterceptorManager interceptorManagerS2C;
    // Executors
    private final ProxyExecutor proxyExecutor;
    private final InterceptorExecutor interceptorExecutorC2S;
    private final InterceptorExecutor interceptorExecutorS2C;
    // Core thread
    private final Thread thread;
    // Core state
    private PetepState state;

    /**
     * Constructs PETEP core.
     * @param proxyModuleContainer Container of proxy modules, which should be started
     * @param interceptorModuleContainerC2S Container of interceptor modules in direction C2S, which should be started
     * @param interceptorModuleContainerS2C Container of interceptor modules in direction S2C, which should be started
     * @param petepListener Listener for listening on PETEP core state changes.
     */
    public PETEP(
            ProxyModuleContainer proxyModuleContainer,
            InterceptorModuleContainer interceptorModuleContainerC2S,
            InterceptorModuleContainer interceptorModuleContainerS2C,
            PetepListener petepListener) {
        this.petepListener = petepListener;

        // Create connection listener manager
        connectionListenerManager = new ConnectionListenerManager();

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

    /**
     * Starts PETEP core.
     */
    public void start() {
        if (state != PetepState.STOPPED) {
            return;
        }

        thread.start();
    }

    /**
     * Stops PETEP core.
     */
    public void stop() {
        if (state != PetepState.STARTED) {
            return;
        }

        thread.interrupt();
    }

    /**
     * Runs PETEP core.
     * <p>
     *     Steps:
     * </p>
     * <ol>
     *     <li>
     *          Prepares all PETEP components.
     *     </li>
     *     <li>
     *          Starts all PETEP components.
     *     </li>
     *     <li>
     *          Wait for interruption.
     *     </li>
     *     <li>
     *          Stops all PETEP components.
     *     </li>
     * </ol>
     *
     */
    private void run() {
        petepListener.beforeCorePrepare(helper);
        if (!runPrepare()) {
            Logger.getGlobal().severe("PETEP failed to prepare!");
        } else {
            petepListener.afterCorePrepare(helper);

            petepListener.beforeCoreStart(helper);
            if (!runStart()) {
                Logger.getGlobal().severe("PETEP failed to start!");
            } else {
                petepListener.afterCoreStart(helper);

                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Logger.getGlobal().info("PETEP interrupted...");
                    Thread.currentThread().interrupt();
                }
            }
        }

        petepListener.beforeCoreStop(helper);
        runStop();
        petepListener.afterCoreStop(helper);
    }

    /**
     * Runs prepare of all PETEP components (proxies, interceptors).
     */
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

    /**
     * Runs start of all PETEP components (proxies, interceptors).
     */
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

    /**
     * Runs stop all PETEP components (proxies, interceptors).
     */
    private void runStop() {
        Logger.getGlobal().info("PETEP stopping...");
        state = PetepState.STOPPING;

        proxyExecutor.stop();
        interceptorExecutorC2S.stop();
        interceptorExecutorS2C.stop();

        state = PetepState.STOPPED;
        Logger.getGlobal().info("PETEP stopped...");
    }

    /**
     * Sends PDU in direction C2S out of PETEP. (Client -&gt; Server)
     * @param pdu PDU to be sent
     */
    public void sendC2S(PDU pdu) {
        pdu.getConnection().sendC2S(pdu);
    }

    /**
     * Sends PDU in direction S2C out of PETEP. (Client &lt;- Server)
     * @param pdu PDU to be sent
     */
    public void sendS2C(PDU pdu) {
        pdu.getConnection().sendS2C(pdu);
    }

    /**
     * Processes PDU internally in direction C2S. (Client -&gt; Server)
     * <p>Sends PDU to appropriate interceptors.</p>
     * @param pdu PDU to be processed
     */
    public void processC2S(PDU pdu) {
        if (pdu.getLastInterceptor() == null) {
            interceptorExecutorC2S.intercept(pdu);
        } else {
            interceptorExecutorC2S.intercept(pdu, pdu.getLastInterceptor().getId() + 1);
        }
    }

    /**
     * Processes PDU internally in direction S2C. (Client &lt;- Server)
     * <p>Sends PDU to appropriate interceptors.</p>
     * @param pdu PDU to be processed
     */
    public void processS2C(PDU pdu) {
        if (pdu.getLastInterceptor() == null) {
            interceptorExecutorS2C.intercept(pdu);
        } else {
            interceptorExecutorS2C.intercept(pdu, pdu.getLastInterceptor().getId() + 1);
        }
    }

    /**
     * Processes PDU internally in direction C2S by sending it in specified interceptor. (Client -&gt; Server)
     * <p>Sends PDU to appropriate interceptors.</p>
     * @param pdu PDU to be processed
     * @param interceptorId Interceptor identifier (zero-based numbering)
     */
    public void processC2S(PDU pdu, int interceptorId) {
        interceptorExecutorC2S.intercept(pdu, interceptorId);
    }

    /**
     * Processes PDU internally in direction S2C by sending it in specified interceptor. (Client &lt;- Server)
     * <p>Sends PDU to appropriate interceptors.</p>
     * @param pdu PDU to be processed
     * @param interceptorId Interceptor identifier (zero-based numbering)
     */
    public void processS2C(PDU pdu, int interceptorId) {
        interceptorExecutorS2C.intercept(pdu, interceptorId);
    }

    /**
     * Get current state of PETEP core.
     * @return Current PETEP core state
     */
    public PetepState getState() {
        return state;
    }

    /**
     * Get helper for PETEP core.
     * @return PETEP core helper
     */
    public PetepHelper getHelper() {
        return helper;
    }

    /**
     * Get proxy manager of current PETEP core
     * @return Current PETEP core proxy manager
     */
    public ProxyManager getProxyManager() {
        return proxyManager;
    }

    /**
     * Get interceptor manager in direction C2S. (Client -&gt; Server)
     * @return Current PETEP core interceptor manager for C2S direction
     */
    public InterceptorManager getInterceptorManagerC2S() {
        return interceptorManagerC2S;
    }

    /**
     * Get interceptor manager in direction S2C. (Client &lt;- Server)
     * @return Current PETEP core interceptor manager for S2C direction
     */
    public InterceptorManager getInterceptorManagerS2C() {
        return interceptorManagerS2C;
    }

    /**
     * Get connection listener manager.
     * @return Current PETEP core listener manager
     */
    public ConnectionListenerManager getConnectionListenerManager() {
        return connectionListenerManager;
    }
}
