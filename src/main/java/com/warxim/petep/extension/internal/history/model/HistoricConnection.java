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
package com.warxim.petep.extension.internal.history.model;

import com.warxim.petep.extension.PetepAPI;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Historic connection model
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@PetepAPI
public class HistoricConnection {
    /**
     * Identifier of the connection in the database
     */
    private Long id;
    /**
     * Code of the connection
     */
    private String code;
    /**
     * Name of the connection
     */
    private String name;
}
