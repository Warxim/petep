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
 * Predicate expression uses Predicate for solve method. Result of Predicate is cached until new
 * parameter is provided.
 */
public class PredicateExpression<T> implements Expression {
  private final Predicate<T> predicate;

  private boolean value;
  private boolean cached;
  private T parameter;

  public PredicateExpression(Predicate<T> predicate) {
    this.predicate = predicate;
    cached = false;
  }

  public PredicateExpression(Predicate<T> function, T param) {
    this(function);
    parameter = param;
  }

  /** Sets predicate parameter. */
  public void setParam(T param) {
    parameter = param;
    cached = false;
  }

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
