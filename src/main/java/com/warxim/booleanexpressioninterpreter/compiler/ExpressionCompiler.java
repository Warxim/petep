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

import com.warxim.booleanexpressioninterpreter.Expression;
import com.warxim.booleanexpressioninterpreter.InvalidExpressionException;

import java.util.regex.Pattern;

/**
 * Expression compiler for compiling string expression into interpreter pattern {@link Expression}.
 */
public class ExpressionCompiler {
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("[0-9^|&!\\ \\t\\n{}\\[\\]()]+");

    private ExpressionCompiler() {}

    /**
     * Compiles string expression into tree of expressions {@link Expression},
     * which uses interpreter pattern to solve boolean expressions.
     * @param expression String expression
     * @param inputs Variables etc. for evaluation
     * @return Compiled expression
     * @throws InvalidExpressionException if the string expression is not valid and cannot be compiled
     */
    public static Expression compile(String expression, Expression[] inputs) throws InvalidExpressionException {
        validateExpression(expression);
        var lexer = new ExpressionLexer(expression);
        var parser = new ExpressionParser(lexer, inputs);
        return parser.build();
    }

    /**
     * Validates expression and throws exception if it is invalid.
     */
    private static void validateExpression(String expression) throws InvalidExpressionException {
        if (!EXPRESSION_PATTERN.matcher(expression).matches()) {
            throw new InvalidExpressionException("Expression contains invalid characters!");
        }
    }
}
