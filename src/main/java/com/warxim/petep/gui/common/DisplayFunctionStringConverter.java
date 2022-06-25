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
package com.warxim.petep.gui.common;

import javafx.util.StringConverter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

/**
 * Class for displaying string obtained using method.
 * <p>Usage: new DisplayFunctionStringConverter&lt;&gt;(SomeClass::getName)</p>
 * <p>If there is null value, empty string will be displayed.</p>
 * <p>Does not implement {@link StringConverter#fromString(String)}, so it works only one way.</p>
 * @param <T> Type of cell item type
 */
@RequiredArgsConstructor
public class DisplayFunctionStringConverter<T> extends StringConverter<T> {
    private final Function<T, String> valueFunction;

    @Override
    public String toString(T object) {
        return object == null ? "" : valueFunction.apply(object);
    }

    @Override
    public T fromString(String string) {
        return null;
    }
}
