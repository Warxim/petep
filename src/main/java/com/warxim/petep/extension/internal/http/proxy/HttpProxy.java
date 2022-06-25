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
package com.warxim.petep.extension.internal.http.proxy;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.internal.http.pdu.HttpPdu;
import com.warxim.petep.extension.internal.http.pdu.WebSocketPdu;
import com.warxim.petep.extension.internal.tcp.TcpConfig;
import com.warxim.petep.extension.internal.tcp.proxy.base.TcpConnection;
import com.warxim.petep.extension.internal.tcp.proxy.base.TcpProxy;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.proxy.module.ProxyModule;

import java.net.Socket;

/**
 * HTTP Proxy supporting simple HTTP and WebSocket PDUs.
 */
public final class HttpProxy extends TcpProxy {
    /**
     * Constructs HTTP proxy
     * @param module Parent module of the worker
     * @param helper Helper for accessing running instance of PETEP core
     * @param config TCP configuration
     */
    public HttpProxy(ProxyModule module, PetepHelper helper, TcpConfig config) {
        super(module, helper, config);
    }

    @Override
    protected TcpConnection createConnection(Socket socket) {
        return new HttpConnection(connectionManager.nextCode(), this, socket);
    }

    @Override
    public boolean supports(PDU pdu) {
        return pdu instanceof HttpPdu || pdu instanceof WebSocketPdu;
    }
}
