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
package com.warxim.petep.extension.internal.tagger;

import com.warxim.petep.extension.PetepAPI;
import com.warxim.petep.extension.internal.tagger.factory.TagSubruleFactory;

/**
 * Interface for registration of tag subrule factories.
 */
@PetepAPI
public interface TaggerApi {
    /**
     * Registers tag subrule factory.
     * @param factory Factory to be registered
     * @return {@code true} if the registration was successfull ({@code false} if the factory code is already registered}
     */
    boolean registerSubruleFactory(TagSubruleFactory factory);
}
