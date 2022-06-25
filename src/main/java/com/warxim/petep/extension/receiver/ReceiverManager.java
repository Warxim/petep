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
package com.warxim.petep.extension.receiver;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Manager for managing receivers.
 * <p>Uses weak references to receivers mapped by their code.</p>
 */
public class ReceiverManager {
    protected final Map<String, WeakReference<Receiver>> receivers;

    /**
     * Constructs receiver manager.
     */
    public ReceiverManager() {
        receivers = new ConcurrentHashMap<>();
    }

    /**
     * Registers receiver.
     * @param receiver Receiver to be registered
     * @return {@code true} if the receiver has been successfully registered ({@code false} if the code is taken)
     */
    public boolean registerReceiver(Receiver receiver) {
        return receivers.putIfAbsent(receiver.getCode(), new WeakReference<>(receiver)) == null;
    }

    /**
     * Unregisters receiver.
     * @param receiver Receiver to unregister
     */
    public void unregisterReceiver(Receiver receiver) {
        receivers.remove(receiver.getCode());
    }

    /**
     * Sends data to receiver.
     * @param code Code of the receiver
     * @param data Data to be sent to the receiver
     * @return {@code true} if the data has been sent to the receiver successfully
     */
    public boolean send(String code, Object data) {
        var reference = receivers.get(code);
        if (reference == null) {
            return false;
        }

        var receiver = reference.get();
        if (receiver == null) {
            receivers.remove(code, reference);
            return false;
        }

        if (!receiver.supports(data.getClass())) {
            return false;
        }

        receiver.receive(data);

        return true;
    }

    /**
     * Obtains list of all registered receivers.
     * @return List of all receivers
     */
    public List<Receiver> getReceivers() {
        return receivers.values().stream()
                .map(WeakReference::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Obtains list of registered receivers that support given class.
     * @param clazz Class to check
     * @return List of receivers that support given class
     */
    public List<Receiver> getReceivers(Class<?> clazz) {
        return receivers.values().stream()
                .map(WeakReference::get)
                .filter(Objects::nonNull)
                .filter(receiver -> receiver.supports(clazz))
                .collect(Collectors.toList());
    }

    /**
     * Unregisters all receivers.
     */
    public void clear() {
        receivers.clear();
    }

}
