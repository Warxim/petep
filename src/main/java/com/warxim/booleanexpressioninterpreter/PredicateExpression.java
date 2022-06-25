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

import java.util.function.Predicate;

/**
 * Predicate expression uses Predicate for solve method.
 * Result of Predicate is cached until new parameter is provided.
 * @param <T> Type of the parameter for predicate
 */
public class PredicateExpression<T> implements Expression {
    private final Predicate<T> predicate;

    private boolean value;
    private boolean cached;
    private T parameter;

    /**
     * Predicate expression
     * <p>
     *     Parameter is null and can be changed using {@link PredicateExpression#setParam}
     * </p>
     * @param predicate Predicate for evaluating the expression
     */
    public PredicateExpression(Predicate<T> predicate) {
        this.predicate = predicate;
        cached = false;
    }

    /**
     * Predicate expression
     * @param predicate Predicate for evaluating the expression
     * @param param parameter for the evaluation of predicate
     */
    public PredicateExpression(Predicate<T> predicate, T param) {
        this(predicate);
        parameter = param;
    }

    /**
     * Sets new predicate parameter.
     * @param param parameter for the evaluation of predicate
     */
    public void setParam(T param) {
        parameter = param;
        cached = false;
    }

    /**
     * Evaluates the predicate using persisted parameter and caches the result.
     */
    @Override
    public boolean solve() {
        if (cached) {
            return value;
        }

        cached = true;
        value = predicate.test(parameter);

        return value;
    }
}
