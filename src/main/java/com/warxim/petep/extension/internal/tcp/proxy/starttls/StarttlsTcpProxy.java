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
package com.warxim.petep.extension.internal.tcp.proxy.starttls;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.internal.tcp.TcpConfig;
import com.warxim.petep.extension.internal.tcp.proxy.base.TcpConnection;
import com.warxim.petep.extension.internal.tcp.proxy.base.TcpPdu;
import com.warxim.petep.extension.internal.tcp.proxy.base.TcpProxy;
import com.warxim.petep.extension.internal.tcp.proxy.base.TcpProxyException;
import com.warxim.petep.extension.internal.tcp.proxy.base.TcpSocketFactory;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.proxy.module.ProxyModule;

import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * STARTTLS TCP Proxy
 * <p>Implementation of STARTTLS protocol support based on TcpProxy.</p>
 */
public final class StarttlsTcpProxy extends TcpProxy  {
    /**
     * TLS Socket factory for wrapping sockets after STARTTLS
     */
    private StarttlsSocketWrapper starttlsSocketWrapper;

    /**
     * Constructs STARTTLS TCP proxy
     * @param module Parent module of the worker
     * @param helper Helper for accessing running instance of PETEP core
     * @param config TCP configuration
     */
    public StarttlsTcpProxy(ProxyModule module, PetepHelper helper, TcpConfig config) {
        super(module, helper, config);
    }

    @Override
    protected TcpConnection createConnection(Socket socket) {
        return new StarttlsTcpConnection(connectionManager.nextCode(), this, socket);
    }

    @Override
    public boolean supports(PDU pdu) {
        return pdu.getClass().equals(TcpPdu.class);
    }

    @Override
    public boolean prepare() {
        if (!super.prepare()) {
            return false;
        }

        // Create socket factory for wrapping plain-text sockets to SSL/TLS
        try {
            starttlsSocketWrapper = new StarttlsSocketWrapper(config.getServerSslConfig(), config.getClientSslConfig());
        } catch (TcpProxyException e) {
            Logger.getGlobal().log(Level.SEVERE, "SSL exception.", e);
            return false;
        }

        return true;
    }

    @Override
    protected TcpSocketFactory createTcpSocketFactory() throws TcpProxyException {
        // Return plain-text TCP socket factory for starting communication
        return new TcpSocketFactory(null, null);
    }

    /**
     * Obtains STARTTLS socket wrapper for wrapping plain-text sockets into SSL/TLS sockets
     * @return STARTTLS socket wrapper
     */
    public StarttlsSocketWrapper getStarttlsSocketWrapper() {
        return starttlsSocketWrapper;
    }
}
