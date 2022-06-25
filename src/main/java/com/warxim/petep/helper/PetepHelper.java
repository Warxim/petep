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

import com.warxim.petep.core.PetepState;
import com.warxim.petep.core.listener.ConnectionListener;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.exception.InactivePetepCoreException;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.interceptor.worker.Interceptor;
import com.warxim.petep.proxy.worker.Proxy;

import java.util.List;
import java.util.Optional;

/**
 * PETEP helper.
 * <p>Allows extensions and modules to work with running instance of PETEP core.</p>
 * <p>When PETEP core stops, this helper will stop working and will throw runtime exception when any method is called.</p>
 */
@PetepAPI
public interface PetepHelper {
    /*
     * GENERAL
     */
    /**
     * Obtains current PETEP core state.
     * @return Current state of PETEP core
     */
    PetepState getState();

    /*
     * PDUS
     */
    /**
     * Processes PDU in PETEP core.
     * <p>Puts PDU into internal PETEP processing, which consists of various cofnigured interceptors.</p>
     * @param pdu PDU to be processed
     * @throws InactivePetepCoreException if the PETEP core is unavailable
     */
    void processPdu(PDU pdu);

    /**
     * Processes PDU internally by sending it in specified interceptor.
     * <p>Sends PDU to appropriate interceptors.</p>
     * @param pdu PDU to be processed
     * @param interceptorId Interceptor identifier (zero-based numbering)
     * @throws InactivePetepCoreException if the PETEP core is unavailable
     */
    void processPdu(PDU pdu, int interceptorId);

    /**
     * Sends PDU outside of the PETEP (to the Internet, ...).
     * @param pdu PDU to be sent
     * @throws InactivePetepCoreException if the PETEP core is unavailable
     */
    void sendPdu(PDU pdu);

    /*
     * PROXIES
     */
    /**
     * Obtains list of active proxies.
     * @return List of proxies, which are enabled and running
     * @throws InactivePetepCoreException if the PETEP core is unavailable
     */
    List<Proxy> getProxies();

    /**
     * Obtains proxy by given code.
     * @param code Code of the proxy
     * @return Proxy
     * @throws InactivePetepCoreException if the PETEP core is unavailable
     * @throws InactivePetepCoreException if the PETEP core is unavailable
     */
    Optional<Proxy> getProxy(String code);

    /*
     * INTERCEPTORS
     */
    /**
     * Obtains list of active interceptors in direction C2S. (Client -&gt; Server)
     * @return List of interceptors, which are enabled
     * @throws InactivePetepCoreException if the PETEP core is unavailable
     */
    List<Interceptor> getInterceptorsC2S();

    /**
     * Obtains list of active interceptors in direction S2C. (Client &lt;- Server)
     * @return List of interceptors, which are enabled
     * @throws InactivePetepCoreException if the PETEP core is unavailable
     */
    List<Interceptor> getInterceptorsS2C();

    /**
     * Obtains interceptor with given code in direction C2S (Client -&gt; Server).
     * @param code Code of the interceptor
     * @return Interceptor if it exists
     * @throws InactivePetepCoreException if the PETEP core is unavailable
     */
    Optional<Interceptor> getInterceptorC2S(String code);

    /**
     * Obtains interceptor with given code in direction S2C (Client &lt;- Server).
     * @param code Code of the interceptor
     * @return Interceptor if it exists
     * @throws InactivePetepCoreException if the PETEP core is unavailable
     */
    Optional<Interceptor> getInterceptorS2C(String code);

    /*
     * LISTENERS
     */
    /**
     * Obtains main connection listener that calls all child listeners.
     * <p>Any call to this listener will call all registered connection listeners.</p>
     * @return Connection listener
     * @throws InactivePetepCoreException if the PETEP core is unavailable
     */
    ConnectionListener getConnectionListener();

    /**
     * Registers connection listener.
     * @param listener Listener to be registered
     * @throws InactivePetepCoreException if the PETEP core is unavailable
     */
    void registerConnectionListener(ConnectionListener listener);

    /**
     * Unregisters connection listener.
     * @param listener Listener to be unregistered
     * @throws InactivePetepCoreException if the PETEP core is unavailable
     */
    void unregisterConnectionListener(ConnectionListener listener);
}
