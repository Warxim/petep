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
package com.warxim.petep.extension.internal.tagger.rule;

import java.util.Collections;
import java.util.List;
import com.warxim.booleanexpressioninterpreter.Expression;
import com.warxim.booleanexpressioninterpreter.ExpressionParser;
import com.warxim.booleanexpressioninterpreter.InvalidExpressionException;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.internal.common.rule_group.Rule;
import com.warxim.petep.extension.internal.tagger.factory.TagSubrule;

/** Tag rule. */
public final class TagRule extends Rule {
  private final String tag;

  private final List<TagSubrule> subrules;

  private final String expressionString;

  private final Expression expression;
  private final PduPredicateExpression[] expressions;

  /** Tag rule constructor. */
  public TagRule(
      String name,
      String description,
      boolean enabled,
      String tag,
      List<TagSubrule> subrules,
      String expressionString) throws InvalidExpressionException {
    super(name, description, enabled);
    this.tag = tag;
    this.subrules = Collections.unmodifiableList(subrules);
    this.expressionString = expressionString;

    // Initialize expressions.
    expressions = new PduPredicateExpression[subrules.size()];

    for (int i = 0; i < expressions.length; ++i) {
      TagSubrule subrule = subrules.get(i);
      expressions[i] = new PduPredicateExpression(subrule::test);
    }

    expression = ExpressionParser.parse(expressionString, expressions);
  }

  public String getTag() {
    return tag;
  }

  public List<TagSubrule> getSubrules() {
    return subrules;
  }

  /*
   * EXPRESSIONS
   */
  public String getExpressionString() {
    return expressionString;
  }

  public boolean test(PDU pdu) {
    if ((!enabled) || pdu.hasTag(tag)) {
      return false;
    }

    return testSubrules(pdu);
  }

  /** Tests subrules using expression (synchronized because of the parameterized expressions). */
  public synchronized boolean testSubrules(PDU pdu) {
    for (int i = 0; i < expressions.length; ++i) {
      expressions[i].setParam(pdu);
    }

    return expression.solve();
  }
}
