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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ExpressionParser parses expressions from string and creates object structure for faster solving.
 */
public final class ExpressionParser {
  private ExpressionParser() {}

  /**
   * Adds spaces to the expression and unites brackets for parser. (This method is automatically
   * called before parsing, so there is no need to call it manually before parsing.)
   *
   * @throws InvalidExpressionException
   */
  public static String prepare(String expression) throws InvalidExpressionException {
    if (expression.length() == 0) {
      return expression;
    }

    StringBuilder builder = new StringBuilder();
    int last = expression.length() - 1;

    for (int i = 0; i < last; ++i) {
      char c = unifySymbol(expression.charAt(i));
      char nextC = unifySymbol(expression.charAt(i + 1));

      if (c > '/' && c < ':') { // Number
        builder.append(c);

        // Next is digit - skip space appending.
        if (nextC > '/' && nextC < ':') {
          continue;
        }
      } else if (c == '&' || c == '|' || c == '^' || c == '(' || c == ')' || c == '!') {
        builder.append(c);
      } else if (c != ' ') {
        throw new InvalidExpressionException("Expression contains invalid character '" + c + "'!");
      }

      // Append space.
      if (nextC != ' ') {
        builder.append(' ');
      }
    }
    builder.append(unifySymbol(expression.charAt(last)));

    return builder.toString().trim();
  }

  /**
   * Parses index from string with specified index limit (number of first non-existing index).
   *
   * @throws InvalidExpressionException (index >= size)
   */
  private static int parseIndex(String index, int limit) throws InvalidExpressionException {
    int l = Integer.parseInt(index);

    if (l >= limit) {
      throw new InvalidExpressionException("Expression contains invalid index (" + index + ")!");
    }

    return l;
  }

  /**
   * Parses boolean expression from string.
   *
   * @throws InvalidExpressionException
   */
  public static Expression parse(String expression, Expression[] parameters)
      throws InvalidExpressionException {
    // Prepare expression for parsing.
    expression = prepare(expression);

    // Create list and fill it with parameters.
    List<Expression> list = new ArrayList<>(Arrays.asList(parameters));

    // Simplify until simplified.
    do {
      int previousLength = expression.length();

      String[] parts = expression.split(" ");

      int end = parts.length - 2;

      // Loop through expression parts.
      for (int i = 0; i <= end; ++i) {
        String left = parts[i];
        String middle = parts[i + 1];

        // NOT syntax.
        if ("!".equals(left)) {
          if (isIndex(middle)) {
            // Save new expression to list.
            list.add(new NotExpression(list.get(parseIndex(middle, list.size()))));

            // Replace !0 with 1.
            expression = expression.replace("! " + middle, String.valueOf(list.size() - 1));

            // Replace following number with new one (negated).
            parts[i + 1] = String.valueOf(list.size() - 1);
          }

          continue;
        }

        // Are we near parts end?
        if (i + 1 > end) {
          continue;
        }

        // Load right parts.
        String right = parts[i + 2];

        // AND / OR / XOR syntax.
        if (isIndex(left) && isOperator(middle) && isIndex(right)) {

          // Save new expression to list.
          list.add(createExpression(middle, list.get(parseIndex(left, list.size())),
              list.get(parseIndex(right, list.size()))));

          // Replace 0 & 1 with 2.
          expression = expression.replace(left + ' ' + middle + ' ' + right,
              String.valueOf(list.size() - 1));

          // Change the right value to new calculated value.
          parts[i + 2] = String.valueOf(list.size() - 1);

          // Move to next value (operator) -> loop will move us from the operator to the next value.
          ++i;
        }
      }

      // Replace all ( n ) with n.
      expression = expression.replaceAll("\\( ([0-9]+) \\)", "$1");

      // Is expression optimized?
      if (isIndex(expression)) {
        break;
      }

      // If the expression has not changed, parse is stuck (invalid expression).
      if (previousLength == expression.length()) {
        throw new InvalidExpressionException(
            "Could not parse expression! Please check if the expression is valid!");
      }
    } while (true);

    return list.get(parseIndex(expression, list.size()));
  }

  /** Is given value an operator? */
  private static boolean isOperator(String str) {
    return "&".equals(str) || "^".equals(str) || "|".equals(str);
  }

  /** Is given value an index? */
  private static boolean isIndex(String str) {
    if (str == null) {
      return false;
    }

    int length = str.length();
    if (length == 0) {
      return false;
    }

    // Check if string contains only numeric characters.
    for (int i = 0; i < length; i++) {
      char c = str.charAt(i);
      if (c <= '/' || c >= ':') {
        return false;
      }
    }

    return true;
  }

  /** Returns expression by given type. */
  private static Expression createExpression(String type, Expression a, Expression b) {
    if ("|".equals(type)) {
      return new OrExpression(a, b);
    }

    if ("&".equals(type)) {
      return new AndExpression(a, b);
    }

    return new XorExpression(a, b);
  }

  /** Unifies symbol. */
  private static char unifySymbol(char symbol) {
    switch (symbol) {
      case '{':
      case '[':
        return '(';
      case '}':
      case ']':
        return ')';
      case '\t':
      case '\n':
        return ' ';
      default:
        return symbol;
    }
  }
}
