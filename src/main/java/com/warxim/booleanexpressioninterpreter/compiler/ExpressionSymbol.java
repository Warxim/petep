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
package com.warxim.booleanexpressioninterpreter.compiler;

/**
 * Expression symbol constants.
 */
class ExpressionSymbol {
    public static final int NONE = -1;
    public static final int INVALID = -2;
    public static final int OR = -3;
    public static final int AND = -4;
    public static final int XOR = -5;
    public static final int NOT = -6;
    public static final int LEFT_BRACKET = -7;
    public static final int RIGHT_BRACKET = -8;

    private ExpressionSymbol() {}
}
