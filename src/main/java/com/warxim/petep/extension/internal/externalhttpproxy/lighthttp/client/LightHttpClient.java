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
package com.warxim.petep.extension.internal.externalhttpproxy.lighthttp.client;

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduQueue;
import com.warxim.petep.extension.internal.externalhttpproxy.EHTTPPConfig;
import com.warxim.petep.extension.internal.externalhttpproxy.lighthttp.LightHttpConstant;
import com.warxim.petep.extension.internal.externalhttpproxy.lighthttp.LightHttpUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Light HTTP client.
 * <p>Sends data to the Light HTTP server through configured HTTP proxy.</p>
 */
public final class LightHttpClient {
    private final EHTTPPConfig config;
    private final PduQueue queue;
    private final LightHttpPduWriter pduWriter;
    private boolean running;

    /**
     * Light HTTP client constructor.
     * @param config Configuration of the EHTTPP module
     * @param queue Queue of PDUs to be processed
     * @param targetInterceptorId Identifier of interceptor to which to send PDUs over HTTP
     */
    public LightHttpClient(EHTTPPConfig config, PduQueue queue, int targetInterceptorId) {
        this.config = config;
        this.queue = queue;

        pduWriter = new LightHttpPduWriter(targetInterceptorId, config.getServerIp(), config.getServerPort());
    }

    /**
     * Starts the client
     */
    public void start() {
        try {
            running = true;

            work();
        } catch (InterruptedException e) {
            // Shutdown
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Stops the client
     */
    public void stop() {
        running = false;
    }

    /**
     * Connects to configured proxy and sends data from queue to it.
     */
    private void work() throws InterruptedException {
        PDU pdu;

        while (running && (pdu = queue.take()) != null) {
            try (var socket = new Socket(config.getProxyIp(), config.getProxyPort());
                 var out = socket.getOutputStream();
                 var in = socket.getInputStream()) {
                pduWriter.write(pdu, out);
                readServerResponse(in);
            } catch (UnknownHostException e) {
                Logger.getGlobal().log(Level.SEVERE, "Unknown host exception in HTTP Proxy!", e);
            } catch (ConnectException e) {
                Logger.getGlobal().log(Level.SEVERE, "Could not connect to HTTP proxy!", e);
            } catch (SocketException e) {
                Logger.getGlobal().log(Level.SEVERE, "Could not connect to HTTP proxy!", e);
                // Interrupted
                return;
            } catch (IOException e) {
                Logger.getGlobal().log(Level.SEVERE, "IO exception in HTTP Proxy!", e);
            }
        }
    }

    /**
     * Reads the whole server response and ignores it.
     */
    private static void readServerResponse(InputStream in) throws IOException {
        // Read response
        LightHttpUtils.skipUntil(in, LightHttpConstant.CR);
        LightHttpUtils.skipNBytes(in, 3); // \n\r\n
    }
}
