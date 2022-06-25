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

import java.util.Optional;

/**
 * PETEP manager allows application to start &amp; stop PETEP core and hands over the resources to the PETEP core.
 */
public final class PetepManager implements PetepListener {
    private final ProxyModuleContainer proxyModuleContainer;
    private final InterceptorModuleContainer interceptorModuleContainerC2S;
    private final InterceptorModuleContainer interceptorModuleContainerS2C;
    private final PetepListenerManager petepListenerManager;

    private PETEP petep;

    /**
     * Constructs PETEP manager for managing PETEP core.
     * @param proxyModuleContainer Container of proxy modules, which should be started
     * @param interceptorModuleContainerC2S Container of interceptor modules in direction C2S, which should be started
     * @param interceptorModuleContainerS2C Container of interceptor modules in direction S2C, which should be started
     * @param petepListenerManager Manager of listeners for listening on PETEP core state changes.
     */
    public PetepManager(
            ProxyModuleContainer proxyModuleContainer,
            InterceptorModuleContainer interceptorModuleContainerC2S,
            InterceptorModuleContainer interceptorModuleContainerS2C,
            PetepListenerManager petepListenerManager) {
        this.proxyModuleContainer = proxyModuleContainer;
        this.interceptorModuleContainerC2S = interceptorModuleContainerC2S;
        this.interceptorModuleContainerS2C = interceptorModuleContainerS2C;
        this.petepListenerManager = petepListenerManager;

        petep = null;

        petepListenerManager.registerListener(this);
    }

    /**
     * Creates and starts PETEP core.
     */
    public synchronized void start() {
        if (petep != null) {
            return;
        }

        petep = new PETEP(proxyModuleContainer, interceptorModuleContainerC2S, interceptorModuleContainerS2C, petepListenerManager);

        petep.start();
    }

    /**
     * Stops PETEP core.
     */
    public synchronized void stop() {
        if (petep == null) {
            return;
        }

        petep.stop();
    }

    /**
     * Obtains PETEP core state.
     * @return State of the PETEP core
     */
    public synchronized PetepState getState() {
        if (petep == null) {
            return PetepState.STOPPED;
        }

        return petep.getState();
    }

    /**
     * Obtains PETEP core helper.
     * @return Helper for the PETEP core
     */
    public synchronized Optional<PetepHelper> getHelper() {
        if (petep == null) {
            return Optional.empty();
        }

        return Optional.of(petep.getHelper());
    }

    /**
     * Obtains the proxy module container.
     * @return Container of proxy modules
     */
    public ProxyModuleContainer getProxyModuleContainer() {
        return proxyModuleContainer;
    }

    /**
     * Obtains interceptor module container for direction C2S. (Client -&gt; Server)
     * @return Container of interceptor module container for direction C2S
     */
    public InterceptorModuleContainer getInterceptorModuleContainerC2S() {
        return interceptorModuleContainerC2S;
    }

    /**
     * Obtains interceptor module container for direction S2C. (Client &lt;- Server)
     * @return Container of interceptor module container for direction S2C
     */
    public InterceptorModuleContainer getInterceptorModuleContainerS2C() {
        return interceptorModuleContainerS2C;
    }

    /**
     * Obtains the PETEP listener manager for registration of core listeners.
     * @return PETEP listener manager
     */
    public PetepListenerManager getPetepListenerManager() {
        return petepListenerManager;
    }

    @Override
    public synchronized void afterCoreStop(PetepHelper helper) {
        petep = null;
    }
}
