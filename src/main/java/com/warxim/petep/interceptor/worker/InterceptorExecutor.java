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
package com.warxim.petep.interceptor.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduQueue;

/** Interceptor executor executes interceptors and creates queues. */
public final class InterceptorExecutor {
  private final InterceptorManager manager;
  private final List<PduQueue> queues;
  private final List<InterceptorWorker> workers;
  private final ExecutorService executor;
  private final Consumer<PDU> consumer;

  public InterceptorExecutor(InterceptorManager manager, Consumer<PDU> consumer) {
    this.manager = manager;
    this.consumer = consumer;

    workers = new ArrayList<>(manager.size());
    queues = new ArrayList<>(manager.size() + 1);

    queues.add(new PduQueue());

    // Create queues and workers with assigned queues.
    for (Interceptor interceptor : manager.getList()) {
      int index = queues.size();

      // Create new queue.
      queues.add(new PduQueue());

      // Create new worker with input and output queues.
      workers.add(new InterceptorWorker(interceptor, queues.get(index - 1), queues.get(index)));
    }

    // Create fixed thread pool for workers and consumer.
    executor = Executors.newFixedThreadPool(queues.size());
  }

  /** Runs prepare method on all interceptors. */
  public boolean prepare() {
    return manager.getList().parallelStream().allMatch(Interceptor::prepare);
  }

  /** Starts interceptors. */
  public void start() {
    // Start all workers.
    for (InterceptorWorker worker : workers) {
      executor.submit(worker);
    }

    // Start consumer work.
    executor.submit(this::work);
  }

  /** Processes PDUs in consumer. */
  private void work() {
    PduQueue queue = queues.get(queues.size() - 1);

    PDU pdu;
    try {
      while ((pdu = queue.take()) != null) {
        consumer.accept(pdu);
      }
    } catch (InterruptedException e) {
      // Closing...
      Thread.currentThread().interrupt();
    }
  }

  /** Stops interceptors. */
  public void stop() {
    // Stop workers.
    executor.shutdownNow();

    // Call stop on interceptors.
    manager.getList().parallelStream().forEach(Interceptor::stop);
  }

  /** Puts PDU into first queue. */
  public void intercept(PDU pdu) {
    queues.get(0).add(pdu);
  }

  /** Puts PDU into specified queue (for specified interceptor). */
  public void intercept(PDU pdu, int interceptorId) {
    queues.get(interceptorId).add(pdu);
  }
}
