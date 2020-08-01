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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.http.pdu.HttpPdu;
import com.warxim.petep.extension.internal.http.pdu.HttpRequestPdu;
import com.warxim.petep.extension.internal.http.pdu.HttpResponsePdu;
import com.warxim.petep.extension.internal.http.pdu.Opcode;
import com.warxim.petep.extension.internal.http.pdu.WebSocketPdu;
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

/** HTTP connection. */
public final class HttpConnection extends TcpConnection {
  /** TCP connection constructor. */
  public HttpConnection(int id, Proxy proxy, Socket socket) {
    super(id, proxy, socket);
  }

  @Override
  protected void readFromClient() {
    try (InputStream in = socketClient.getInputStream()) {
      PduReader reader = new HttpRequestReader(in, getConfig().getBufferSize(),
          Charset.forName(getConfig().getCharset()));

      PDU pdu;
      while ((pdu = reader.read()) != null) {
        pdu.setProxy(proxy);
        pdu.setConnection(this);

        if (pdu instanceof HttpRequestPdu) {
          String temp = ((HttpRequestPdu) pdu).getHeader("Upgrade");
          if (temp != null && "websocket".equals(temp)) {
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
    try (InputStream in = socketServer.getInputStream()) {
      PduReader reader = new HttpResponseReader(in, getConfig().getBufferSize(),
          Charset.forName(getConfig().getCharset()));

      PDU pdu;
      while ((pdu = reader.read()) != null) {
        pdu.setProxy(proxy);
        pdu.setConnection(this);

        if (pdu instanceof HttpResponsePdu) {
          // HTTP
          String temp = ((HttpResponsePdu) pdu).getHeader("Upgrade");
          if (temp != null && "websocket".equals(temp)) {
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

    try (OutputStream out = socketClient.getOutputStream()) {
      PduWriter writer = new HttpResponseWriter(out);

      // Read bytes to buffer and send it to out stream
      while ((pdu = queueS2C.take()) != null) {
        writer.write(pdu);

        if (pdu instanceof HttpResponsePdu) {
          // HTTP
          String temp = ((HttpResponsePdu) pdu).getHeader("Upgrade");
          if (temp != null && "websocket".equals(temp)) {
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

    try (OutputStream out = socketServer.getOutputStream()) {
      PduWriter writer = new HttpRequestWriter(out);
      // Read bytes to buffer and send it to out stream
      while ((pdu = queueC2S.take()) != null) {
        writer.write(pdu);

        if (pdu instanceof HttpRequestPdu) {
          String temp = ((HttpRequestPdu) pdu).getHeader("Upgrade");
          if (temp != null && "websocket".equals(temp)) {
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
