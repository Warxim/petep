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

import java.util.function.BiPredicate;

/**
 * BiPredicate expression uses BiPredicate for solve method.
 * Result of BiPredicate is cached until new parameters are provided.
 * @param <T> Type of the first parameter for bipredicate
 * @param <U> Type of the second parameter for bipredicate
 */
public class BiPredicateExpression<T, U> implements Expression {
    private final BiPredicate<T, U> predicate;

    private boolean value;
    private boolean cached;
    private T firstParameter;
    private U secondParameter;

    /**
     * BiPredicate expression
     * <p>
     *     Parameters are null and can be changed using {@link BiPredicateExpression#setParams}
     * </p>
     * @param predicate BiPredicate for evaluating the expression
     */
    public BiPredicateExpression(BiPredicate<T, U> predicate) {
        this.predicate = predicate;
        cached = false;
    }

    /**
     * BiPredicate expression
     * @param predicate BiPredicate for evaluating the expression
     * @param firstParam first parameter for the evaluation of bipredicate
     * @param secondParam second parameter for the evaluation of bipredicate
     */
    public BiPredicateExpression(BiPredicate<T, U> predicate, T firstParam, U secondParam) {
        this(predicate);
        firstParameter = firstParam;
        secondParameter = secondParam;
    }

    /**
     * Sets new parameters for bipredicate evaluation.
     * @param firstParam first parameter for the evaluation of bipredicate
     * @param secondParam second parameter for the evaluation of bipredicate
     */
    public void setParams(T firstParam, U secondParam) {
        firstParameter = firstParam;
        secondParameter = secondParam;
        cached = false;
    }


    /**
     * Evaluates the bipredicate using persisted parameters and caches the result.
     */
    @Override
    public boolean solve() {
        if (cached) {
            return value;
        }

        cached = true;
        value = predicate.test(firstParameter, secondParameter);

        return value;
    }
}
