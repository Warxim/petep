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
package com.warxim.petep.extension.internal.tcp.proxy.base;

/**
 * TCP proxy exception signalizing that something went wrong during proxy startup.
 */
public class TcpProxyException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs TCP proxy exception.
     * @param message Description of the problem
     * @param cause Cause of the problem
     */
    public TcpProxyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs TCP proxy exception.
     * @param message Description of the problem
     */
    public TcpProxyException(String message) {
        super(message);
    }
}
