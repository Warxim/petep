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
package com.warxim.petep.extension.internal.externalhttpproxy.lighthttp.server;

import com.warxim.petep.extension.internal.externalhttpproxy.EHTTPPConfig;
import com.warxim.petep.extension.internal.externalhttpproxy.lighthttp.LightHttpConstant;
import com.warxim.petep.extension.internal.externalhttpproxy.lighthttp.exception.InvalidDataException;
import com.warxim.petep.helper.PetepHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Light HTTP server.
 */
public final class LightHttpServer {
    private final PetepHelper helper;
    private final EHTTPPConfig config;
    private final LightHttpPduReader pduReader;
    private ServerSocket socket;
    private boolean running;

    /**
     * Light HTTP server constructor.
     * @param helper Helper for accessing currently running core
     * @param config Configuration of the EHTTPP module
     */
    public LightHttpServer(PetepHelper helper, EHTTPPConfig config) {
        this.helper = helper;
        this.config = config;
        this.pduReader = new LightHttpPduReader(helper);
    }

    /**
     * Runs server.
     */
    public void run() {
        try {
            socket = new ServerSocket(config.getServerPort(), 0, InetAddress.getByName(config.getServerIp()));

            Logger.getGlobal().info("HTTP Server started on " + config.getServerIp() + ":" + config.getServerPort() + "!");

            running = true;

            while (running) {
                accept();
            }
        } catch (UnknownHostException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not start HTTP server! (The host is unknown!)", e);
        } catch (SocketException e) {
            // Interrupted
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not start HTTP server!", e);
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                Logger.getGlobal().log(Level.SEVERE, "Could not close HTTP server!", e);
            }
        }
        Logger.getGlobal().log(Level.INFO, "HTTP server stopped.");
    }

    /**
     * Stops server.
     */
    public void stop() {
        running = false;

        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            Logger.getGlobal().log(Level.SEVERE, "Could not close HTTP server!", e);
        }
    }

    /**
     * Accepts new connections.
     */
    private void accept() {
        try (var client = socket.accept();
             var in = client.getInputStream();
             var out = client.getOutputStream()) {
            processReadFromClient(in, out);
        } catch (SocketException e) {
            // Interrupted
        } catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE, "Exception during HTTP read / write!", e);
        }
    }

    /**
     * Reads data from client input stream.
     * @throws IOException – If there is problem with reading the PDU from the output stream
     */
    private void processReadFromClient(InputStream in, OutputStream out) throws IOException {
        try {
            var pdu = pduReader.read(in);

            if (pdu == null) {
                out.write(LightHttpConstant.RESPONSE_DESERIALIZATION_ERROR);
                return;
            }

            helper.processPdu(pdu);

            out.write(LightHttpConstant.RESPONSE_OK);
        } catch (InvalidDataException e) {
            out.write(e.getMessageBytes());
        }
    }
}
