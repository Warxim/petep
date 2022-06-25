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
package com.warxim.petep.extension.internal.tagger.factory.internal.ends_with;

import com.warxim.petep.extension.internal.tagger.factory.TagSubruleData;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.nio.charset.Charset;

/**
 * Tag subrule data for "endsWith" subrule.
 */
@Value
@EqualsAndHashCode(callSuper=true)
public class EndsWithData extends TagSubruleData {
    byte[] data;
    Charset charset;
}
