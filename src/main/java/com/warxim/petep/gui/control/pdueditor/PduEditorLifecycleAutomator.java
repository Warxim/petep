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
package com.warxim.petep.gui.control.pdueditor;

import com.warxim.petep.core.PetepState;
import com.warxim.petep.core.listener.PetepListener;
import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.helper.ExtensionHelper;
import com.warxim.petep.helper.PetepHelper;
import javafx.application.Platform;
import lombok.RequiredArgsConstructor;

/**
 * Life-cycle automator for PDU editor.
 * <p>
 *     Automatically loads and unloads PDU editor on PETEP core start/stop events.
 * </p>
 */
@RequiredArgsConstructor
@PetepAPI
public class PduEditorLifecycleAutomator implements PetepListener {
    private final PduEditor editor;

    /**
     * Starts life-cycle automator for PDU editor.
     * @param extensionHelper Extension helper
     */
    public void start(ExtensionHelper extensionHelper) {
        extensionHelper.registerPetepListener(this);

        // Determine correct initialization method based on core state
        var state = extensionHelper.getPetepState();
        if (state == PetepState.STARTED) {
            // Core is started, try to use PETEP helper for loading
            var maybeHelper = extensionHelper.getPetepHelper();
            if (maybeHelper.isPresent()) {
                editor.load(maybeHelper.get());
                return;
            }
        }
        editor.unload();
    }

    /**
     * Stops life-cycle automator for PDU editor.
     * @param extensionHelper Extension helper
     */
    public void stop(ExtensionHelper extensionHelper) {
        extensionHelper.unregisterPetepListener(this);
    }

    @Override
    public void afterCoreStart(PetepHelper helper) {
        Platform.runLater(() -> editor.load(helper));
    }

    @Override
    public void beforeCoreStop(PetepHelper helper) {
        Platform.runLater(editor::unload);
    }
}
