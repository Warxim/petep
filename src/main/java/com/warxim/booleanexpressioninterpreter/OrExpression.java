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
package com.warxim.booleanexpressioninterpreter;

/**
 * Logical OR expression.
 * <p>
 *     Used for creating and solving expression:{@code left || right}.
 * </p>
 */
public final class OrExpression implements Expression {
    private final Expression left;
    private final Expression right;

    /**
     * Logical OR expression
     * @param left expression on the left side of operator
     * @param right expression on the right side of operator
     */
    public OrExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Solves the expression {@code left || right}
     * @return result of the expression {@code left || right}
     */
    @Override
    public boolean solve() {
        return left.solve() || right.solve();
    }
}
