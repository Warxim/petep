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

import com.warxim.booleanexpressioninterpreter.*;

/**
 * Expression parser that uses recursive descent parser to build expression tree.
 */
class ExpressionParser {
    private final ExpressionLexer lexer;
    private final Expression[] variables;

    private Expression expression;
    private int symbol;

    /**
     * Constructs expression parser using given lexer and variables.
     * @param lexer Lexer for providing symbols from expression
     * @param variables Input variables
     */
    public ExpressionParser(ExpressionLexer lexer, Expression[] variables) {
        this.lexer = lexer;
        this.variables = variables;
        this.symbol = ExpressionSymbol.NONE;
    }

    /**
     * Builds expression.
     * @return Constructed expression
     * @throws InvalidExpressionException If the expression is invalid
     */
    public Expression build() throws InvalidExpressionException {
        processOrExpressions();
        return expression;
    }

    /**
     * Processes OR expressions.
     */
    private void processOrExpressions() throws InvalidExpressionException {
        processXorExpressions();
        while (symbol == ExpressionSymbol.OR) {
            var left = expression;
            processXorExpressions();
            expression = new OrExpression(left, expression);
        }
    }

    /**
     * Processes XOR expressions.
     */
    private void processXorExpressions() throws InvalidExpressionException {
        processAndExpressions();
        while (symbol == ExpressionSymbol.XOR) {
            var left = expression;
            processAndExpressions();
            expression = new XorExpression(left, expression);
        }
    }

    /**
     * Processes AND expressions.
     */
    private void processAndExpressions() throws InvalidExpressionException {
        processFactor();
        while (symbol == ExpressionSymbol.AND) {
            var left = expression;
            processFactor();
            expression = new AndExpression(left, expression);
        }
    }

    /**
     * Processes variables, not expressions and left brackets.
     */
    private void processFactor() throws InvalidExpressionException {
        symbol = lexer.nextSymbol();
        if (symbol >= 0 && symbol < variables.length) {
            // Handle variable
            expression = variables[symbol];
            symbol = lexer.nextSymbol();
        } else if (symbol == ExpressionSymbol.NOT) {
            // Handle NOT
            processFactor();
            expression = new NotExpression(expression);
        } else if (symbol == ExpressionSymbol.LEFT_BRACKET) {
            // Handle left bracket
            processOrExpressions();
            symbol = lexer.nextSymbol();
        } else {
            throw new InvalidExpressionException("Invalid expression!");
        }
    }
}
