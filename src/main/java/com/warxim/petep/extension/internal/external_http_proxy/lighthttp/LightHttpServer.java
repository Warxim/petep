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
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.extension.internal.external_http_proxy.EHTTPPConfig;
import com.warxim.petep.extension.internal.external_http_proxy.lighthttp.exception.InvalidDataException;
import com.warxim.petep.helper.PetepHelper;
import com.warxim.petep.proxy.worker.Proxy;

/** Light HTTP server. */
public final class LightHttpServer {

  private final PetepHelper helper;

  private final EHTTPPConfig config;

  private ServerSocket socket;

  private boolean running;

  private final int lastInterceptorC2SIndex;
  private final int lastInterceptorS2CIndex;

  /** Light HTTP server constructor. */
  public LightHttpServer(PetepHelper helper, EHTTPPConfig config) {
    this.helper = helper;
    this.config = config;
    this.lastInterceptorC2SIndex = helper.getInterceptorsC2S().size() + 1;
    this.lastInterceptorS2CIndex = helper.getInterceptorsS2C().size() + 1;
  }

  /** Runs server. */
  public void run() {
    try {
      socket =
          new ServerSocket(config.getServerPort(), 0, InetAddress.getByName(config.getServerIp()));

      Logger.getGlobal()
          .info("HTTP Server started on " + config.getServerIp() + ":" + config.getServerPort()
              + "!");

      running = true;

      while (running) {
        accept();
      }
    } catch (UnknownHostException e) {
      Logger.getGlobal()
          .log(Level.SEVERE, "Could not start HTTP server! (The host is unknown!)", e);
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

  /** Stops server. */
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

  /** Accepts new connections. */
  private void accept() {
    try (Socket client = socket.accept();
        InputStream in = client.getInputStream();
        OutputStream out = client.getOutputStream()) {
      try {
        process(in, out);
      } catch (InvalidDataException e) {
        out.write(e.getMessageBytes());
      }
    } catch (SocketException e) {
      // Interrupted
    } catch (Exception e) {
      Logger.getGlobal().log(Level.SEVERE, "Exception during HTTP read / write!", e);
    }
  }

  private Proxy readProxy(InputStream in, StringBuilder builder)
      throws IOException, InvalidDataException {
    int currentByte;
    // Read proxy code
    while ((currentByte = LightHttpUtils.readByte(in)) != 0x2F) {
      builder.append((char) currentByte);
    }

    Proxy proxy = helper.getProxy(builder.toString());
    if (proxy == null) {
      throw new InvalidDataException(LightHttpConstant.RESPONSE_WRONG_PROXY);
    }

    return proxy;
  }

  private Connection readConnection(InputStream in, StringBuilder builder, Proxy proxy)
      throws IOException, InvalidDataException {
    int currentByte;
    // Read client ID
    while ((currentByte = LightHttpUtils.readByte(in)) != 0x2F) {
      builder.append((char) currentByte);
    }

    Connection connection = proxy.getConnectionManager().get(Integer.parseInt(builder.toString()));

    if (connection == null) {
      throw new InvalidDataException(LightHttpConstant.RESPONSE_WRONG_CONNECTION);
    }

    return connection;
  }

  private PduDestination readDestination(InputStream in) throws IOException {
    PduDestination destination;

    if (LightHttpUtils.readByte(in) == LightHttpConstant.S2C) {
      destination = PduDestination.CLIENT;
    } else {
      destination = PduDestination.SERVER;
    }

    return destination;
  }

  private int readTargetInterceptor(InputStream in, StringBuilder builder) throws IOException {
    int currentByte;
    // Read target interceptor id
    while ((currentByte = LightHttpUtils.readByte(in)) != 0x2F) {
      // End of stream reached.
      builder.append((char) currentByte);
    }
    return Integer.parseInt(builder.toString());
  }

  private void validateTargetInterceptor(PduDestination destination, int targetInterceptorId)
      throws InvalidDataException {
    if (targetInterceptorId < 0
        || (destination == PduDestination.SERVER && targetInterceptorId >= lastInterceptorC2SIndex)
        || (destination == PduDestination.CLIENT
            && targetInterceptorId >= lastInterceptorS2CIndex)) {
      throw new InvalidDataException(LightHttpConstant.RESPONSE_WRONG_INTERCEPTOR);
    }
  }


  /**
   * Reads data from client input stream. TODO: change skip to skipNBytes in Java 12.
   * 
   * @throws InvalidDataException
   */
  private void process(InputStream in, OutputStream out) throws IOException, InvalidDataException {
    // Skip POST /
    LightHttpUtils.skipNBytes(6, in);

    StringBuilder builder = new StringBuilder();

    // Proxy
    Proxy proxy = readProxy(in, builder);

    builder.setLength(0);

    // Connection
    Connection connection = readConnection(in, builder, proxy);

    builder.setLength(0);

    int targetInterceptorId = readTargetInterceptor(in, builder);

    // Read destination
    PduDestination destination = readDestination(in);

    // Check validity of interceptor id.
    validateTargetInterceptor(destination, targetInterceptorId);

    // Skip HTTP/1.0\r\n
    LightHttpUtils.skipNBytes(11, in);

    // Read headers
    Map<String, String> serializedMetaData = new HashMap<>();
    Set<String> tags = new HashSet<>();
    Charset charset = null;
    int contentLength = 0;

    int currentByte;

    while (true) {
      // Read first character to detect end of headers
      currentByte = LightHttpUtils.readByte(in);

      if (currentByte == LightHttpConstant.CR) {
        // skip \n\r\n
        LightHttpUtils.skipNBytes(1, in);
        break;
      }

      // Read key
      builder.setLength(0);
      builder.append((char) currentByte);
      while ((currentByte = LightHttpUtils.readByte(in)) != LightHttpConstant.COLON) {
        builder.append((char) currentByte);
      }

      String temp = builder.toString();


      // Skip space after :
      LightHttpUtils.skipNBytes(1, in);

      builder.setLength(0);

      if (LightHttpConstant.CONTENT_LENGTH_HEADER.equals(temp)) {
        contentLength = readContentLength(in, builder);
      } else if (LightHttpConstant.CONTENT_TYPE_HEADER.equals(temp)) {
        charset = readCharset(in, builder);
      } else if (LightHttpConstant.TAGS_HEADER.equals(temp)) {
        readTags(in, builder, tags);
      } else if (temp.startsWith(LightHttpConstant.METADATA_HEADER_START)) {
        serializedMetaData.put(temp.substring(2), readMetadata(in, builder));
      } else {
        skipHeader(in);
      }

      // Skip \n
      LightHttpUtils.skipNBytes(1, in);
    }

    // Read data.
    byte[] buffer = in.readNBytes(contentLength);

    // Create PDU.
    PDU pdu = proxy.getModule()
        .getFactory()
        .getDeserializer()
        .deserializePdu(proxy, connection, destination, buffer, contentLength, tags,
            serializedMetaData);

    if (pdu == null) {
      out.write(LightHttpConstant.RESPONSE_DESERIALIZATION_ERROR);
      return;
    }

    // Set charset
    if (charset != null) {
      pdu.setCharset(charset);
    }

    // Process PDU in PETEP.
    helper.processPdu(pdu, targetInterceptorId);

    out.write(LightHttpConstant.RESPONSE_OK);
  }

  private void skipHeader(InputStream in) throws IOException {
    // Unknown header, skip value.
    while (LightHttpUtils.readByte(in) != LightHttpConstant.CR) {
      // Read until \r
    }
  }

  private int readContentLength(InputStream in, StringBuilder builder) throws IOException {
    int currentByte;
    // Read until \r
    while ((currentByte = LightHttpUtils.readByte(in)) != LightHttpConstant.CR) {
      builder.append((char) currentByte);
    }

    return Integer.parseInt(builder.toString());
  }

  private String readMetadata(InputStream in, StringBuilder builder) throws IOException {
    int currentByte;
    // Read until \r
    while ((currentByte = LightHttpUtils.readByte(in)) != LightHttpConstant.CR) {
      builder.append((char) currentByte);
    }

    return builder.toString();
  }

  private void readTags(InputStream in, StringBuilder builder, Set<String> tags)
      throws IOException {
    int currentByte;
    // Read until \r
    while ((currentByte = LightHttpUtils.readByte(in)) != LightHttpConstant.CR) {
      if (currentByte == LightHttpConstant.TAGS_SEPARATOR) {
        tags.add(builder.toString());
        builder.setLength(0);
      } else {
        builder.append((char) currentByte);
      }
    }

    tags.add(builder.toString());
  }

  private Charset readCharset(InputStream in, StringBuilder builder) throws IOException {
    // Skip text/plain; charset=
    LightHttpUtils.skipNBytes(20, in);

    int currentByte;

    // Read until 0x0D = \r
    while ((currentByte = LightHttpUtils.readByte(in)) != LightHttpConstant.CR) {
      builder.append((char) currentByte);
    }

    return Charset.forName(builder.toString());
  }
}
