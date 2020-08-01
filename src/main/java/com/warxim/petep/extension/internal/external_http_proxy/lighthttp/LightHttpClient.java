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
package com.warxim.petep.extension.internal.external_http_proxy.lighthttp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.core.pdu.PduQueue;
import com.warxim.petep.extension.internal.external_http_proxy.EHTTPPConfig;

/**
 * Light HTTP client.
 */
public final class LightHttpClient {
  private final byte[] firstLineStart;

  private final EHTTPPConfig config;

  private final PduQueue queue;

  private final int targetInterceptorId;

  private boolean running;

  /** Light HTTP client constructor. */
  public LightHttpClient(EHTTPPConfig config, PduQueue queue, int targetId) {
    this.config = config;
    this.queue = queue;
    this.targetInterceptorId = targetId;

    // Create first line.
    byte[] serverIpBytes = config.getServerIp().getBytes();
    byte[] serverPortBytes = String.valueOf(config.getServerPort()).getBytes();

    ByteBuffer buffer = ByteBuffer.allocate(LightHttpConstant.FIRST_LINE_START.length + 2
        + serverIpBytes.length + serverPortBytes.length);
    buffer.put(LightHttpConstant.FIRST_LINE_START);
    buffer.put(serverIpBytes);
    buffer.put(LightHttpConstant.COLON);
    buffer.put(serverPortBytes);
    buffer.put(LightHttpConstant.SLASH);

    firstLineStart = buffer.array();
  }

  public void start() {
    try {
      running = true;

      work();
    } catch (InterruptedException e) {
      // Shutdown
      Thread.currentThread().interrupt();
    }
  }

  public void stop() {
    running = false;
  }

  private void work() throws InterruptedException {
    PDU pdu;

    while (running && (pdu = queue.take()) != null) {
      while (running && pdu != null) {
        try (Socket socket = new Socket(config.getProxyIp(), config.getProxyPort());
            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream()) {
          writePduToOutput(pdu, out);
          readFromInput(in);

          pdu = null;
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
  }

  private void readFromInput(InputStream in) throws IOException {
    // Read response
    while (LightHttpUtils.readByte(in) != LightHttpConstant.CR) {
      // Read until \r
    }

    LightHttpUtils.skipNBytes(3, in); // \n\r\n
  }

  private void writePduToOutput(PDU pdu, OutputStream out) throws IOException {
    // First line
    writeFirstLineToOutput(pdu, out);

    // Tags
    writeTagsToOutput(pdu.getTags(), out);

    // Metadata
    writeMetadataToOutput(
        pdu.getProxy().getModule().getFactory().getSerializer().serializePduMetadata(pdu), out);

    // Content length
    out.write(LightHttpConstant.CONTENT_LENGTH);
    out.write(String.valueOf(pdu.getSize()).getBytes());
    out.write(LightHttpConstant.HEADER_END);

    // Content type
    out.write(LightHttpConstant.CONTENT_TYPE_CHARSET);
    out.write(pdu.getCharset().name().getBytes());

    // End headers
    out.write(LightHttpConstant.HEADERS_END);

    // Data
    out.write(pdu.getBuffer(), 0, pdu.getSize());
  }

  private void writeFirstLineToOutput(PDU pdu, OutputStream out) throws IOException {
    // First line start
    out.write(firstLineStart);

    // Proxy
    out.write(pdu.getProxy().getModule().getCode().getBytes());
    out.write(LightHttpConstant.SLASH);
    // Connection
    out.write(String.valueOf(pdu.getConnection().getId()).getBytes());
    out.write(LightHttpConstant.SLASH);
    // Target Interceptor Id
    out.write(String.valueOf(targetInterceptorId).getBytes());
    out.write(LightHttpConstant.SLASH);
    // Destination
    if (pdu.getDestination() == PduDestination.CLIENT) {
      out.write(LightHttpConstant.S2C);
    } else {
      out.write(LightHttpConstant.C2S);
    }

    // End first line
    out.write(LightHttpConstant.FIRST_LINE_END);
  }

  private void writeTagsToOutput(Set<String> tags, OutputStream out) throws IOException {
    if (!tags.isEmpty()) {
      out.write(LightHttpConstant.TAGS_HEADER_BYTES);

      int remaining = tags.size();
      for (String tag : tags) {
        out.write(tag.getBytes());

        if (--remaining != 0) {
          out.write(LightHttpConstant.TAGS_SEPARATOR);
        }
      }
      out.write(LightHttpConstant.HEADER_END);
    }
  }

  private void writeMetadataToOutput(Map<String, String> serializedMetaData, OutputStream out)
      throws IOException {
    if (serializedMetaData != null) {
      for (var item : serializedMetaData.entrySet()) {
        out.write(LightHttpConstant.METADATA_HEADER_START_BYTES);
        out.write(item.getKey().getBytes());
        out.write(LightHttpConstant.HEADER_COLON);
        out.write(item.getValue().getBytes());
        out.write(LightHttpConstant.HEADER_END);
      }
    }
  }
}
