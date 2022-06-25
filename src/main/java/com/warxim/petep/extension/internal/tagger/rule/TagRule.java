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

import com.warxim.booleanexpressioninterpreter.Expression;
import com.warxim.booleanexpressioninterpreter.InvalidExpressionException;
import com.warxim.booleanexpressioninterpreter.compiler.ExpressionCompiler;
import com.warxim.petep.core.pdu.PDU;
import com.warxim.petep.extension.internal.common.rulegroup.Rule;
import com.warxim.petep.extension.internal.tagger.factory.TagSubrule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tag rule.
 * <p>Consists of multiple subrules, which are evaluated and used to evaluate rule expression.</p>
 */
public final class TagRule extends Rule {
    private final String tag;
    private final List<TagSubrule> subrules;
    private final String expressionString;
    private final Expression expression;
    private final PduPredicateExpression[] expressions;

    /**
     * Tag rule constructor.
     * @param name Name of the rule
     * @param description Description of the rule
     * @param enabled {@code true} if the rule should be used
     * @param tag Tag to set on PDUs
     * @param subrules List of subrules
     * @param expressionString Expression representing the condition using subrules
     * @throws InvalidExpressionException If the provided expression was not valid
     */
    public TagRule(
            String name,
            String description,
            boolean enabled,
            String tag,
            List<TagSubrule> subrules,
            String expressionString) throws InvalidExpressionException {
        super(name, description, enabled);
        this.tag = tag.intern();
        this.subrules = Collections.unmodifiableList(subrules);
        this.expressionString = expressionString;

        // Initialize expressions.
        expressions = new PduPredicateExpression[subrules.size()];

        for (int i = 0; i < expressions.length; ++i) {
            var subrule = subrules.get(i);
            expressions[i] = new PduPredicateExpression(subrule::test);
        }

        expression = ExpressionCompiler.compile(expressionString, expressions);
    }

    /**
     * Obtains tag, which should be added if the rule test returns {@code true} for tested PDU.
     * @return Tag to be added to matching PDU
     */
    public String getTag() {
        return tag;
    }

    /**
     * Gets list of all subrules in this rule.
     * @return List of tag subrules
     */
    public List<TagSubrule> getSubrules() {
        return subrules;
    }

    /**
     * Gets expression string.
     * @return Original expression string
     */
    public String getExpressionString() {
        return expressionString;
    }

    /**
     * Tests the given PDU using test subrules in configured expression.
     * @param pdu PDU to be tested
     * @return {@code true} if the PDU matches the rule.
     */
    public boolean test(PDU pdu) {
        if ((!enabled) || pdu.hasTag(tag)) {
            return false;
        }

        return testSubrules(pdu);
    }

    /**
     * Tests subrules using expression (synchronized because of the parameterized expressions with cache).
     */
    private synchronized boolean testSubrules(PDU pdu) {
        for (int i = 0; i < expressions.length; ++i) {
            expressions[i].setParam(pdu);
        }

        return expression.solve();
    }

    /**
     * Creates deep copy of the Tag Rule.
     * @return Deep copy of the Tag Rule
     */
    public TagRule copy() {
        try {
            List<TagSubrule> subrulesCopy = new ArrayList<>(subrules.size());
            for (var subrule : subrules) {
                subrulesCopy.add(subrule.copy());
            }
            subrulesCopy = Collections.unmodifiableList(subrulesCopy);
            return new TagRule(name, description, enabled, tag, subrulesCopy, expressionString);
        } catch (InvalidExpressionException e) {
            throw new IllegalStateException("Invalid expression - this should never happen, because the expression was already parsed before.");
        }
    }
}
