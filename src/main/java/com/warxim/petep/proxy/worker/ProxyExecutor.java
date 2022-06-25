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
package com.warxim.petep.proxy.worker;

/**
 * Proxy executor executes proxy workers.
 */
public final class ProxyExecutor {
    /**
     * Manager that manages all active proxies
     */
    private final ProxyManager manager;

    /**
     * Constructs proxy executor.
     * @param manager Proxy manager that handles all active proxies
     */
    public ProxyExecutor(ProxyManager manager) {
        this.manager = manager;
    }

    /**
     * Runs prepare method in all proxy workers.
     * @return  {@code true} if all proxies have been successfully prepared;<br>
     *          {@code false} if at least one of proxies has failed preparation (this will abort start of PETEP core)
     */
    public boolean prepare() {
        return manager.getList().parallelStream().allMatch(Proxy::prepare);
    }

    /**
     * Runs start method in all proxy workers.
     * @return  {@code true} if all proxies have been successfully started;<br>
     *          {@code false} if at least one of proxies has failed to start (this will abort start of PETEP core)
     */
    public boolean start() {
        return manager.getList().parallelStream().allMatch(Proxy::start);
    }

    /**
     * Runs stop method in all proxy workers.
     */
    public void stop() {
        manager.getList().parallelStream().forEach(Proxy::stop);
    }
}
