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
package com.warxim.petep.extension.internal.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import com.warxim.petep.core.connection.Connection;
import com.warxim.petep.core.pdu.PduDestination;
import com.warxim.petep.core.pdu.PduQueue;
import com.warxim.petep.proxy.worker.Proxy;
import com.warxim.petep.util.PduUtils;

public final class TestConnection extends Connection {
  private boolean running;
  private final ExecutorService executor;

  public TestConnection(int id, Proxy proxy) {
    super(id, proxy);
    executor = Executors.newFixedThreadPool(4);
  }

  @Override
  public boolean start() {
    running = true;

    executor.execute(() -> doWrite('A', queueC2S));
    executor.execute(() -> doWrite('B', queueS2C));
    executor.execute(() -> doRead('A'));
    executor.execute(() -> doRead('B'));

    Logger.getGlobal().info("Test connection " + id + " started.");

    return true;
  }

  private boolean testPdu(TestPdu pdu) {
    byte[] data = pdu.getBuffer();
    byte[] expected = ((TestProxy) proxy).getConfig().getBytesToReceive();

    if (data.length != expected.length) {
      return false;
    }

    for (int i = 0; i < data.length; ++i) {
      if (data[i] != expected[i]) {
        return false;
      }
    }

    if (!pdu.hasTag("test_tag_1") || !pdu.hasTag("test_tag_2")) {
      return false;
    }

    return pdu.hasTag("test_tag_1") && pdu.hasTag("test_tag_2")
        && pdu.getTest().equals("test_string");
  }

  public void doRead(char direction) {
    PduDestination destination = (direction == 'A') ? PduDestination.SERVER : PduDestination.CLIENT;

    while (running) {

      TestPdu pdu =
          new TestPdu(proxy, this, destination, ((TestProxy) proxy).getConfig().getBytesToSend(),
              ((TestProxy) proxy).getConfig().getBytesToSend().length, "test_string");

      pdu.addTag("test_tag_1");
      pdu.addTag("test_tag_2");

      process(pdu);

      Logger.getGlobal().info("Test connection " + id + " sent data to PETEP [" + direction + "].");

      try {
        Thread.sleep(((TestProxy) proxy).getConfig().getSendDelay());
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }

  public void doWrite(char direction, PduQueue queue) {
    try {
      TestPdu pdu;

      while ((pdu = (TestPdu) queue.take()) != null) {
        if (testPdu(pdu)) {
          Logger.getGlobal()
              .info("Test connection " + id + " sent data to the black hole [" + direction + "].");
        } else {
          Logger.getGlobal()
              .severe("Test connection " + id + " detected corrupted data [" + direction + "]: "
                  + PduUtils.bufferToString(pdu));
        }
      }
    } catch (InterruptedException e) {
      // Shutdown
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public void stop() {
    Logger.getGlobal().info("Stopping test connection " + id + "...");
    running = false;

    // Shutdown threads.
    executor.shutdownNow();

    // Remove connection from connection manager.
    ((TestConnectionManager) proxy.getConnectionManager()).remove(this);

    Logger.getGlobal().info("Test connection " + id + " stopped.");
  }
}
