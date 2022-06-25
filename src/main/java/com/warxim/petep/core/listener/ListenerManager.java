/*
 * PEnetration TEsting Proxy (PETEP)
 *
 * Copyright (C) 2021 Michal Válka
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
package com.warxim.petep.core.listener;

import java.lang.ref.WeakReference;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Listener Manager for managing multiple listeners.
 * <p>
 *     Uses WeakReference, so that the listener resources are automatically freed,
 *     when the reference becomes weakly reachable.
 *     (To prevent leaks caused by forgotten unregister call.)
 * </p>
 * <p>
 *     Listeners have to be stored as strong reference.
 * </p>
 * @param <L> Listener type
 */
public class ListenerManager<L> {
    protected final CopyOnWriteArrayList<WeakReference<L>> listeners;

    /**
     * Constructs listener manager.
     */
    public ListenerManager() {
        listeners = new CopyOnWriteArrayList<>();
    }

    /**
     * Register listener
     * <p>Stores weak reference to the listener.</p>
     * @param listener Listener to be registered
     */
    public void registerListener(L listener) {
        listeners.add(new WeakReference<>(listener));
    }

    /**
     * Unregister listener
     * <p>Removes listener weak reference from the manager.</p>
     * @param listener Listener to be unregistered
     */
    public void unregisterListener(L listener) {
        listeners.removeIf(reference -> listener.equals(reference.get()));
    }

    /**
     * Call the specified consumer for all listeners
     * <p>
     *     Calls {@link Consumer#accept(Object)} for each listener (listener as a parameter).
     * </p>
     * @param consumer Consumer for handling the listener call
     */
    protected void call(Consumer<L> consumer) {
        listeners.forEach(reference -> callListener(reference, consumer));
    }

    /**
     * Call the specified consumer for all listeners in parallel
     * <p>
     *     Calls {@link Consumer#accept(Object)} for each listener (listener as a parameter) using parallel stream.
     * </p>
     * @param consumer Consumer for handling the listener call
     */
    protected void parallelCall(Consumer<L> consumer) {
        listeners.parallelStream().forEach(reference -> callListener(reference, consumer));
    }

    /**
     * Removes all listeners.
     */
    public void clear() {
        listeners.clear();
    }

    /**
     * Call the consumer with the listener as parameter (if it is still referenced)
     * <p>Removes the listener reference from listeners, if it does not exist.</p>
     * @param reference Listener reference
     * @param consumer Consumer for handling the listener call
     */
    private void callListener(WeakReference<L> reference, Consumer<L> consumer) {
        var listener = reference.get();
        if (listener == null) {
            listeners.remove(reference);
            return;
        }
        consumer.accept(listener);
    }
}
