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
package com.warxim.petep.extension.internal.modifier.factory.internal.replace;

import com.warxim.petep.extension.internal.modifier.factory.ModifierData;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.nio.charset.Charset;

/**
 * Replacer data for replacing bytes in PDUs.
 */
@Value
@EqualsAndHashCode(callSuper=false)
public class ReplacerData extends ModifierData {
    /**
     * Which occurrence to replace (-1 for all, zero-numbered).
     */
    int occurrence;
    byte[] what;
    Charset whatCharset;
    byte[] with;
    Charset withCharset;
}
