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
package com.warxim.petep.extension.internal.logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.warxim.petep.common.Constant;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.core.pdu.PduQueue;
import com.warxim.petep.proxy.serizalization.ProxySerializer;
import com.warxim.petep.proxy.worker.Proxy;
import com.warxim.petep.util.FileUtils;
import com.warxim.petep.util.PduUtils;

/** Logger worker. */
public final class LoggerWorker implements Runnable {
  private static final int RECORDS_PER_FILE = 10000;
  private final PduQueue queue;
  private final String path;
  private final SimpleDateFormat formatter;

  /** Logger worker constructor. */
  public LoggerWorker(String path) {
    this.queue = new PduQueue();
    this.path = path;
    this.formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
  }

  public void log(PDU pdu) {
    queue.add(pdu);
  }

  @Override
  public void run() {
    Logger.getGlobal().info("Logger for path '" + path + "' started.");

    Path source = Paths.get(FileUtils.getProjectFileAbsolutePath(path));

    while (log()) {
      // Create backup
      try {
        if (source.getFileName().toString().contains(".")) {
          // [FILE_NAME].backup_[timestamp].[EXTENSION]
          Files.move(source,
              source.resolveSibling(source.getFileName()
                  .toString()
                  .replaceFirst("(.+)[.]([^.]+)$",
                      "$1.backup_" + (System.currentTimeMillis() / 100) + ".$2")));
        } else {
          // [FILE_NAME]_backup_[timestamp]
          Files.move(source, source.resolveSibling(
              source.getFileName() + "_backup_" + (System.currentTimeMillis() / 100)));
        }
      } catch (IOException e) {
        Logger.getGlobal().log(Level.SEVERE, "Logger encountered an exception.", e);
      }
    }
  }

  /** Logging to file. Returns true if logging should continue. */
  private boolean log() {
    try (PrintWriter writer = new PrintWriter(
        new BufferedWriter(new FileWriter(FileUtils.getProjectFileAbsolutePath(path), true)))) {
      PDU pdu;

      int counter = 0;
      while ((pdu = queue.take()) != null) {
        // Info
        Proxy proxy = pdu.getProxy();
        writer.write("###############################################\r\nTime: ");
        writer.write(formatter.format(new Date()));
        writer.write("\r\nProxy: ");
        writer.write(proxy.getModule().getCode());
        writer.write("\r\nDestination: ");
        writer.write(pdu.getDestination() == PduDestination.CLIENT ? "client" : "server");
        writer.write("\r\nConnection: ");
        writer.write(String.valueOf(pdu.getConnection().getId()));

        // Tags
        Set<String> tags = pdu.getTags();
        if (!tags.isEmpty()) {
          writer.write("\r\nTags: ");
          writer.write(PduUtils.tagsToString(tags));
        }

        // Meta data
        ProxySerializer serializer = proxy.getModule().getFactory().getSerializer();
        if (serializer != null) {
          Map<String, String> metadata = serializer.serializePduMetadata(pdu);

          if (metadata != null && !metadata.isEmpty()) {
            writer.write("\r\nMeta data:\r\n");
            for (var item : metadata.entrySet()) {
              writer.write("\t");
              writer.write(item.getKey());
              writer.write(": ");
              writer.write(item.getValue());
              writer.write("\r\n");
            }
          } else {
            writer.write("\r\n");
          }
        } else {
          writer.write("\r\n");
        }

        // Data
        writer.write("Data [bytes]:\r\n-----------------------------\r\n");
        writer.write(PduUtils.bufferToString(pdu));
        writer.write(
            "\r\n-----------------------------\r\nData [string]:\r\n-----------------------------\r\n");
        writer.write(new String(pdu.getBuffer(), 0, pdu.getSize(), Constant.DEFAULT_CHARSET));
        writer.write("\r\n-----------------------------\r\n\r\n");

        // Create backup.
        if (++counter == RECORDS_PER_FILE) {
          return true;
        }

        // Save the buffered data if there are no PDUs waiting in the queue.
        if (queue.isEmpty()) {
          writer.flush();
        }
      }
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Logger stopped with exception.", e);
    } catch (InterruptedException e) {
      // Interrupted.
      Thread.currentThread().interrupt();
    }

    return false;
  }
}
