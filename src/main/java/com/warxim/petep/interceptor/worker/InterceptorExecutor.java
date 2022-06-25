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

import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.core.pdu.PduQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Interceptor executor executes interceptors and creates queues.
 */
public final class InterceptorExecutor {
    /**
     * Interceptor manager that manages all active interceptors
     */
    private final InterceptorManager manager;

    /**
     * Consumer for consuming PDUs after processing in interceptors
     */
    private final Consumer<PDU> consumer;

    /**
     * List of PDU queues (one for each interceptor, one for sending PDUs to consumer)
     */
    private final List<PduQueue> queues;

    /**
     * List of interceptor workers which handle intercepting in separate threads (one for each interceptor)
     */
    private final List<InterceptorWorker> workers;

    /**
     * Executor service for running workers and for sending PDUs to consumer
     */
    private final ExecutorService executor;

    /**
     * Constructs interceptor executor.
     * @param manager Interceptor manager that handles all active interceptors
     * @param consumer Consumer for consuming PDUs after processing in interceptors
     */
    public InterceptorExecutor(InterceptorManager manager, Consumer<PDU> consumer) {
        this.manager = manager;
        this.consumer = consumer;

        workers = new ArrayList<>(manager.size());
        queues = new ArrayList<>(manager.size() + 1);

        queues.add(new PduQueue());

        // Create queues and workers with assigned queues.
        for (var interceptor : manager.getList()) {
            int index = queues.size();

            // Create new queue.
            queues.add(new PduQueue());

            // Create new worker with input and output queues.
            workers.add(new InterceptorWorker(interceptor, queues.get(index - 1), queues.get(index)));
        }

        // Create fixed thread pool for workers and consumer.
        executor = Executors.newFixedThreadPool(queues.size());
    }

    /**
     * Runs prepare method on all interceptors.
     * @return  {@code true} if all interceptors have been successfully prepared;<br>
     *          {@code false} if at least one of interceptors has failed preparation (this will abort start of PETEP core)
     */
    public boolean prepare() {
        return manager.getList().parallelStream().allMatch(Interceptor::prepare);
    }

    /**
     * Starts interceptors.
     */
    public void start() {
        // Start all workers.
        for (var worker : workers) {
            executor.submit(worker);
        }

        // Start consumer work.
        executor.submit(this::work);
    }

    /**
     * Processes PDUs in consumer.
     */
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

    /**
     * Stops interceptors.
     */
    public void stop() {
        // Stop workers.
        executor.shutdownNow();

        // Call stop on interceptors.
        manager.getList().parallelStream().forEach(Interceptor::stop);
    }

    /**
     * Puts PDU into first queue for processing by first interceptor.
     * @param pdu PDU to be processed
     */
    public void intercept(PDU pdu) {
        queues.get(0).add(pdu);
    }

    /**
     * Puts PDU into specified queue (for specified interceptor).
     * @param interceptorId Interceptor identifier (zero-based numbering)
     * @param pdu PDU to be processed
     */
    public void intercept(PDU pdu, int interceptorId) {
        queues.get(interceptorId).add(pdu);
    }
}
