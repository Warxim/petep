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
package com.warxim.petep.extension.internal.common.gui;

import javafx.application.Platform;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Reloadable controller base for controllers that can be loaded and unloaded.
 * <p>This base allows to unload more "lazily" by using delayed unload.</p>
 */
public abstract class ReloadableController {
    /**
     * Timer for scheduling unload.
     */
    private Timer unloadTimer;
    /**
     * {@code true} if the data are loaded.
     */
    private boolean isLoaded;

    /**
     * Constructs reloadable controller.
     */
    protected ReloadableController() {
        isLoaded = false;
    }

    /**
     * Loads the controller
     * <p>Cancels scheduled unload if it exists and calls handleLoad in JavaFX context.</p>
     */
    public synchronized void load() {
        if (unloadTimer != null) {
            unloadTimer.cancel();
            unloadTimer = null;
            return;
        }
        if (isLoaded) {
            return; // Skip load if already loaded
        }
        isLoaded = true;
        Platform.runLater(this::handleLoad);
    }

    /**
     * Unloads the controller without any delay
     */
    public synchronized void unload() {
        if (unloadTimer != null) {
            unloadTimer.cancel();
        }

        runUnload();
    }

    /**
     * Reloads the controller.
     */
    public synchronized void reload() {
        unload();
        load();
    }

    /**
     * Unloads the controller after specified delay
     * @param delayMillis Delay in milliseconds after which the unload will happen
     */
    public synchronized void unload(int delayMillis) {
        if (unloadTimer != null) {
            unloadTimer.cancel();
        }
        unloadTimer = new Timer();
        unloadTimer.schedule(new TimerTask() {
            public void run() {
                if (unloadTimer == null) {
                    return;
                }
                runUnload();
            }
        }, delayMillis);
    }

    /**
     * Processes unload of the controller
     */
    private synchronized void runUnload() {
        if (unloadTimer != null) {
            unloadTimer.cancel();
            unloadTimer = null;
        }
        if (!isLoaded) {
            return; // Skip unload if already unloaded
        }
        isLoaded = false;
        Platform.runLater(this::handleUnload);
    }

    /**
     * Handles the load (loads data, components etc.)
     */
    protected abstract void handleLoad();

    /**
     * Handles the unload (unloads data, components etc.)
     */
    protected abstract void handleUnload();

    /**
     * Check whether the controller is loaded
     * @return {@code true} if the controller is loaded
     */
    public boolean isLoaded() {
        return isLoaded;
    }
}
