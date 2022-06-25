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

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;

/**
 * Expression lexer tokenizes given expression and provides symbols for parsing.
 */
class ExpressionLexer {
    private final StreamTokenizer tokenizer;

    /**
     * Constructs expression lexer, which tokenizes given expression and provides symbols for parsing.
     * @param expression String expression for lexing
     */
    public ExpressionLexer(String expression) {
        tokenizer = new StreamTokenizer(new StringReader(expression));
        tokenizer.resetSyntax();
        tokenizer.wordChars('0', '9');
        tokenizer.whitespaceChars(' ', ' ');
        tokenizer.whitespaceChars('\t', '\t');
        tokenizer.whitespaceChars('\n', '\n');
        tokenizer.ordinaryChar('(');
        tokenizer.ordinaryChar('{');
        tokenizer.ordinaryChar('[');
        tokenizer.ordinaryChar(')');
        tokenizer.ordinaryChar('}');
        tokenizer.ordinaryChar(']');
        tokenizer.ordinaryChar('&');
        tokenizer.ordinaryChar('|');
        tokenizer.ordinaryChar('^');
        tokenizer.ordinaryChar('!');
    }

    /**
     * Obtains next symbol from expression.
     * @return Symbol from expression;
     *         <br>if &lt; 0 then contains operators etc. (see constants in {@link ExpressionSymbol})
     *         <br>if &gt;= 0 then contains input variable index
     */
    public int nextSymbol() {
        try {
            switch (tokenizer.nextToken()) {
                case StreamTokenizer.TT_WORD:
                    return Integer.parseInt(tokenizer.sval);
                case '(':
                case '[':
                case '{':
                    return ExpressionSymbol.LEFT_BRACKET;
                case ')':
                case ']':
                case '}':
                    return ExpressionSymbol.RIGHT_BRACKET;
                case '^':
                    return ExpressionSymbol.XOR;
                case '&':
                    return ExpressionSymbol.AND;
                case '|':
                    return ExpressionSymbol.OR;
                case '!':
                    return ExpressionSymbol.NOT;
                default:
                    return ExpressionSymbol.INVALID;
            }
        } catch (IOException e) {
            return ExpressionSymbol.INVALID;
        }
    }
}
