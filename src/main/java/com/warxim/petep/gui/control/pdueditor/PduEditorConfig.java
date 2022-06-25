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

import com.warxim.petep.extension.PetepAPI;
import lombok.Builder;
import lombok.Value;

/**
 * PDU editor configuration
 */
@Value
@Builder(toBuilder = true)
@PetepAPI
public class PduEditorConfig {
    /**
     * If the editor is strict, it means it will show only proxies and connection that can work with given PDU.
     */
    @Builder.Default
    boolean strict = true;

    /**
     * If the automatic lifecycle is enabled, PDU editor will load / unload automatically when PETEP core starts/stops.
     * <p><b>Note:</b> Disabling automatic lifecycle is useful, when you need to access editor before core stop event.</p>
     */
    @Builder.Default
    boolean automaticLifecycle = true;
}
