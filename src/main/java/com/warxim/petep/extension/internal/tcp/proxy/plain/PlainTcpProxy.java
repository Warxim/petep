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
package com.warxim.petep.extension.internal.tcp.proxy.plain;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.internal.tcp.TcpConfig;
import com.warxim.petep.extension.internal.tcp.proxy.base.TcpConnection;
import com.warxim.petep.extension.internal.tcp.proxy.base.TcpPdu;
import com.warxim.petep.extension.internal.tcp.proxy.base.TcpProxy;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.proxy.module.ProxyModule;

import java.net.Socket;

/**
 * TCP Proxy.
 * <p>Simple implementation for basic TCP support.</p>
 * <p>Supports plain-text, SSL/TLS, but does not support STARTTLS, for STARTTLS, use {@link com.warxim.petep.extension.internal.tcp.proxy.starttls.StarttlsTcpProxy}.</p>
 */
public final class PlainTcpProxy extends TcpProxy  {
    /**
     * Constructs plain TCP proxy
     * @param module Parent module of the worker
     * @param helper Helper for accessing running instance of PETEP core
     * @param config TCP configuration
     */
    public PlainTcpProxy(ProxyModule module, PetepHelper helper, TcpConfig config) {
        super(module, helper, config);
    }

    @Override
    protected TcpConnection createConnection(Socket socket) {
        return new PlainTcpConnection(connectionManager.nextCode(), this, socket);
    }

    @Override
    public boolean supports(PDU pdu) {
        return pdu.getClass().equals(TcpPdu.class);
    }
}
