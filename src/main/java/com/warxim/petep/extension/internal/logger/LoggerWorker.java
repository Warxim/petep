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

import com.warxim.petep.common.Constant;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.core.pdu.PduQueue;
import com.warxim.petep.util.FileUtils;
import com.warxim.petep.util.PduUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logger worker.
 * <p>One logger worker for each log file.</p>
 * <p>Creates backup after certain number of records is reached ands starts writing to a new file.</p>
 */
public final class LoggerWorker implements Runnable {
    private static final int RECORDS_PER_FILE = 10000;
    private final PduQueue queue;
    private final String path;

    /**
     * Logger worker constructor.
     * @param path Path to log file
     */
    public LoggerWorker(String path) {
        this.queue = new PduQueue();
        this.path = path;
    }

    /**
     * Adds the PDU to the queue for logging.
     * @param pdu PDU to be logged
     */
    public void log(PDU pdu) {
        queue.add(pdu);
    }

    @Override
    public void run() {
        Logger.getGlobal().info(() -> String.format("Logger for path '%s' started.", path));

        var source = Paths.get(FileUtils.getProjectFileAbsolutePath(path));

        while (log()) {
            // Create backup
            try {
                var newLocation = generateBackupFilePath(source);
                Files.move(source, newLocation);
            } catch (IOException e) {
                Logger.getGlobal().log(Level.SEVERE, "Logger encountered an exception.", e);
            }
        }
    }

    /**
     * Generates backup file path for log file.
     */
    private Path generateBackupFilePath(Path logFile) {
        var originalFileName = logFile.getFileName().toString();
        if (originalFileName.contains(".")) {
            // [FILE_NAME].backup_[timestamp].[EXTENSION]
            var fileName = originalFileName.replaceFirst(
                    "(.+)[.]([^.]+)$",
                    "$1.backup_" + (System.currentTimeMillis() / 100) + ".$2");
            return logFile.resolveSibling(fileName);
        }
        // [FILE_NAME]_backup_[timestamp]
        var fileName = originalFileName + "_backup_" + (System.currentTimeMillis() / 100);
        return logFile.resolveSibling(fileName);
    }

    /**
     * Logging to file. Returns true if logging should continue.
     */
    private boolean log() {
        try (var writer = new PrintWriter(new BufferedWriter(new FileWriter(FileUtils.getProjectFileAbsolutePath(path), Constant.DEFAULT_CHARSET, true)))) {
            PDU pdu;

            var counter = 0;
            while ((pdu = queue.take()) != null) {
                // Write PDU
                writePduToLog(writer, pdu);

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

    /**
     * Writes PDU to log using provided writer.
     */
    private void writePduToLog(PrintWriter writer, PDU pdu) {
        // Info
        var proxy = pdu.getProxy();
        writer.write("###############################################\r\nTime: ");
        writer.write(Constant.DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        writer.write("\r\nProxy: ");
        writer.write(proxy.getModule().getCode());
        writer.write("\r\nDestination: ");
        writer.write(pdu.getDestination() == PduDestination.CLIENT ? "client" : "server");
        writer.write("\r\nConnection: ");
        writer.write(String.valueOf(pdu.getConnection().getCode()));

        // Tags
        var tags = pdu.getTags();
        if (!tags.isEmpty()) {
            writer.write("\r\nTags: ");
            writer.write(PduUtils.tagsToString(tags));
        }

        // Meta data
        var metadata = proxy.getModule().getFactory().getSerializer().serializePduMetadata(pdu);
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

        // Data
        writer.write("Data [bytes]:\r\n-----------------------------\r\n");
        writer.write(PduUtils.bufferToHexString(pdu));
        writer.write("\r\n-----------------------------\r\nData [string]:\r\n-----------------------------\r\n");
        writer.write(new String(pdu.getBuffer(), 0, pdu.getSize(), Constant.DEFAULT_CHARSET));
        writer.write("\r\n-----------------------------\r\n\r\n");

    }
}
