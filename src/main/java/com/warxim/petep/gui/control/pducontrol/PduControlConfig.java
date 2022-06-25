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
package com.warxim.petep.gui.control.pducontrol;

import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.gui.control.pdueditor.PduEditorConfig;
import lombok.Builder;
import lombok.Value;

import java.util.Collections;
import java.util.List;

/**
 * PDU component configuration
 */
@Value
@Builder(toBuilder = true)
@PetepAPI
public class PduControlConfig {
    /**
     * Configuration of PDU editor, which is displayed in PDU control with active PETEP core
     */
    @Builder.Default
    PduEditorConfig editorConfig = PduEditorConfig.builder().build();

    /**
     * List of buttons displayed when view is active
     */
    @Builder.Default
    List<PduControlButton> viewButtons = Collections.emptyList();

    /**
     * List of buttons displayed when editor is active
     */
    @Builder.Default
    List<PduControlButton> editorButtons = Collections.emptyList();
}

