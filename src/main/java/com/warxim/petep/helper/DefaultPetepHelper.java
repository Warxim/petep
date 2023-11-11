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

import com.warxim.petep.core.PETEP;
import com.warxim.petep.core.PetepState;
import com.warxim.petep.core.listener.ConnectionListener;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.exception.InactivePetepCoreException;
import com.warxim.petep.interceptor.worker.Interceptor;
import com.warxim.petep.proxy.worker.Proxy;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of PETEP helper.
 * <p>
 *     Stores weak reference to PETEP and everytime method is called, checks whether PETEP the reference has been cleared.
 * </p>
 */
public final class DefaultPetepHelper implements PetepHelper {
    private final WeakReference<PETEP> petepReference;

    /**
     * Constructor of default PETEP helper implementation.
     * @param petep PETEP core to wrap inside this helper
     */
    public DefaultPetepHelper(PETEP petep) {
        this.petepReference = new WeakReference<>(petep);
    }

    @Override
    public PetepState getState() {
        return getPetepOrThrowException().getState();
    }

    @Override
    public void processPdu(PDU pdu) {
        getPetepOrThrowException().process(pdu);
    }

    @Override
    public void processPdu(PDU pdu, int interceptorId) {
        getPetepOrThrowException().process(pdu, interceptorId);
    }

    @Override
    public void sendPdu(PDU pdu) {
        getPetepOrThrowException().send(pdu);
    }

    @Override
    public List<Proxy> getProxies() {
        return getPetepOrThrowException().getProxyManager().getList();
    }

    @Override
    public Optional<Proxy> getProxy(String code) {
        return Optional.ofNullable(getPetepOrThrowException().getProxyManager().get(code));
    }

    @Override
    public List<Interceptor> getInterceptorsC2S() {
        return getPetepOrThrowException().getInterceptorManagerC2S().getList();
    }

    @Override
    public List<Interceptor> getInterceptorsS2C() {
        return getPetepOrThrowException().getInterceptorManagerS2C().getList();
    }

    @Override
    public Optional<Interceptor> getInterceptorC2S(String code) {
        return Optional.ofNullable(getPetepOrThrowException().getInterceptorManagerC2S().get(code));
    }

    @Override
    public Optional<Interceptor> getInterceptorS2C(String code) {
        return Optional.ofNullable(getPetepOrThrowException().getInterceptorManagerS2C().get(code));
    }

    @Override
    public ConnectionListener getConnectionListener() {
        return getPetepOrThrowException().getConnectionListenerManager();
    }

    @Override
    public void registerConnectionListener(ConnectionListener listener) {
        getPetepOrThrowException().getConnectionListenerManager().registerListener(listener);
    }

    @Override
    public void unregisterConnectionListener(ConnectionListener listener) {
        getPetepOrThrowException().getConnectionListenerManager().unregisterListener(listener);
    }

    /**
     * Obtains PETEP or throws runtime exception if it is null.
     */
    private PETEP getPetepOrThrowException() {
        var petep = petepReference.get();
        if (petep == null) {
            throw new InactivePetepCoreException();
        }
        return petep;
    }
}
