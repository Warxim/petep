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
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.http.pdu.*;
import com.warxim.petep.extension.internal.http.reader.HttpRequestReader;
import com.warxim.petep.extension.internal.http.reader.HttpResponseReader;
import com.warxim.petep.extension.internal.http.reader.PduReader;
import com.warxim.petep.extension.internal.http.reader.WebSocketReader;
import com.warxim.petep.extension.internal.http.writer.HttpRequestWriter;
import com.warxim.petep.extension.internal.http.writer.HttpResponseWriter;
import com.warxim.petep.extension.internal.http.writer.PduWriter;
import com.warxim.petep.extension.internal.http.writer.WebSocketWriter;
import com.warxim.petep.extension.internal.tcp.proxy.base.TcpConnection;
import com.warxim.petep.proxy.worker.Proxy;

import java.io.IOException;
import java.net.Socket;

/**
 * HTTP connection.
 * <p>Handles both HTTP and WebSocket streams.</p>
 */
public final class HttpConnection extends TcpConnection {
    private static final String UPGRADE_HEADER = "Upgrade";
    private static final String UPGRADE_HEADER_WEBSOCKET = "websocket";

    /**
     * TCP connection constructor.
     * @param code Unique code of the connection
     * @param proxy Proxy to which the connection belongs
     * @param socket Client socket for the connection
     */
    public HttpConnection(String code, Proxy proxy, Socket socket) {
        super(code, proxy, socket);
    }

    @Override
    protected void readFromClient() {
        try (var in = socketClient.getInputStream()) {
            PduReader reader = new HttpRequestReader(
                    in,
                    getConfig().getBufferSize(),
                    getConfig().getCharset());

            PDU pdu;
            while ((pdu = reader.read()) != null) {
                pdu.setProxy(proxy);
                pdu.setConnection(this);

                if (pdu instanceof HttpRequestPdu) {
                    var temp = ((HttpRequestPdu) pdu).getHeader(UPGRADE_HEADER);
                    if (UPGRADE_HEADER_WEBSOCKET.equals(temp)) {
                        reader = new WebSocketReader(in, PduDestination.SERVER);
                        // Do not support any extensions.
                        ((HttpPdu) pdu).removeHeader("Sec-WebSocket-Extensions");
                    }
                }

                process(pdu);
            }
        } catch (IOException e) {
            // Closed
        }
    }

    @Override
    protected void readFromServer() {
        try (var in = socketServer.getInputStream()) {
            PduReader reader = new HttpResponseReader(
                    in,
                    getConfig().getBufferSize(),
                    getConfig().getCharset());

            PDU pdu;
            while ((pdu = reader.read()) != null) {
                pdu.setProxy(proxy);
                pdu.setConnection(this);

                if (pdu instanceof HttpResponsePdu) {
                    // HTTP
                    var temp = ((HttpResponsePdu) pdu).getHeader(UPGRADE_HEADER);
                    if (UPGRADE_HEADER_WEBSOCKET.equals(temp)) {
                        reader = new WebSocketReader(in, PduDestination.CLIENT);
                    }
                } else {
                    // WebSockets
                    if (((WebSocketPdu) pdu).getOpcode() == Opcode.CLOSE) {
                        socketServer.close();
                    }
                }

                process(pdu);
            }
        } catch (IOException e) {
            // Closed
        }
    }

    @Override
    protected void writeToClient() {
        PDU pdu;

        try (var out = socketClient.getOutputStream()) {
            PduWriter writer = new HttpResponseWriter(out);

            // Read bytes to buffer and send it to out stream
            while ((pdu = queueS2C.take()) != null) {
                writer.write(pdu);

                if (pdu instanceof HttpResponsePdu) {
                    // HTTP
                    var temp = ((HttpResponsePdu) pdu).getHeader(UPGRADE_HEADER);
                    if (UPGRADE_HEADER_WEBSOCKET.equals(temp)) {
                        writer = new WebSocketWriter(out);
                    }
                } else {
                    // WebSockets
                    if (((WebSocketPdu) pdu).getOpcode() == Opcode.CLOSE) {
                        socketClient.close();
                    }
                }
            }
        } catch (IOException e) {
            // Closed socket
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    protected void writeToServer() {
        PDU pdu;

        try (var out = socketServer.getOutputStream()) {
            PduWriter writer = new HttpRequestWriter(out);
            // Read bytes to buffer and send it to out stream
            while ((pdu = queueC2S.take()) != null) {
                writer.write(pdu);

                if (pdu instanceof HttpRequestPdu) {
                    var temp = ((HttpRequestPdu) pdu).getHeader(UPGRADE_HEADER);
                    if (UPGRADE_HEADER_WEBSOCKET.equals(temp)) {
                        writer = new WebSocketWriter(out);
                    }
                }
            }
        } catch (IOException e) {
            // Closed socket
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
