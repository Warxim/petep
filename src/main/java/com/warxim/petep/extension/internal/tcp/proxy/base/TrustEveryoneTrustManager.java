/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2021 Michal Válka
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
package com.warxim.petep.extension.internal.tcp.proxy.base;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import java.net.Socket;
import java.security.cert.X509Certificate;

/**
 * Trust manager for trusting everyone.
 */
public class TrustEveryoneTrustManager extends X509ExtendedTrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
        // Trust everyone.
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
        // Trust everyone.
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) {
        // Trust everyone.
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) {
        // Trust everyone.
    }

    @Override
    public void checkClientTrusted(
            X509Certificate[] x509Certificates,
            String s,
            SSLEngine sslEngine) {
        // Trust everyone.
    }

    @Override
    public void checkServerTrusted(
            X509Certificate[] x509Certificates,
            String s,
            SSLEngine sslEngine) {
        // Trust everyone.
    }
}
