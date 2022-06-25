package com.warxim.booleanexpressioninterpreter;

import com.warxim.booleanexpressioninterpreter.compiler.ExpressionCompiler;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BooleanExpressionInterpreterTest {
    private static final VariableExpression TRUE_EXPRESSION = new VariableExpression(true);
    private static final VariableExpression FALSE_EXPRESSION = new VariableExpression(false);

    private static final VariableExpression[] EXPRESSIONS = new VariableExpression[]{
            FALSE_EXPRESSION,
            TRUE_EXPRESSION,
            FALSE_EXPRESSION,
            TRUE_EXPRESSION
    };

    @DataProvider(name = "simpleExpressions")
    public Object[][] simpleExpressionsProvider() {
        return new Object[][]{
                {"0", false},
                {"1", true},
                {"!0", true},
                {"!1", false},
                {"!!1", true},
                {"!!!1", false},
                {"1 & 0", false},
                {"1 & !0", true},
                {"1 | 0", true},
                {"!!0", false},
                {"!!!0", true},
                {"1 & 1", true},
                {"1 & (0 | 1)", true},
                {"1 & 0 | 1", true},
                {"1 & 1 & 1", true},
                {"1 & 1 & 0", false},
                {"1 & (1) & 0", false},
                {"1 & !!(1) | 0", true},
                {"1 & 1 & !0", true},
                {"1 & (1 & (!0))", true},
                {"{[1 & [[({{{1}} & (!0)})]]]}", true},
                {"1 & (1 & (!0)) & 0", false},
                {"1 & (1 & (!0)) & 1", true},
                {"((1 & 1) & !!!0 & (!!(0 | 1)))", true},
                {"((1 & 1) & !!!0 & [!!(0 | 1)]", true},
                {"0 | 1 & 1", true},
                {"1 | 0 & 0", true},
                {"(1 | 0) & 0", false},
                {"(1 | 0) & 1 & (1 & 0)", false},
                {"0 ^ 0", false},
                {"0 ^ 1", true},
                {"0 ^ 1", true},
                {"!1 | 0", false},
                {"(1 | 0) & 3", true},
                {"(1 | 0) & 2", false},
                {"[{((([1 | 0]))) & [(3)]}]", true},
                {"[{((([1 | 0]))) & [(2)]}]", false},
                {"0 | 2 | 0 | 3", true},
                {"3 & 0 & 0", false},
                {"3 & 0 & 0 | 3", true},
                {"3 & 0 & (0 | 3)", false},
                {"!(!1 | 0) & [!{0}]", true},
                {" ! (  !  1  |  0)  &   [!{ 0   }  ] ", true},
                {" !! (  !  1  |  0)  &   [!{ 0   }  ] ", false},
                {" !!! (  !  1  |  0)  &   [!{ 0   }  ] ", true},
        };
    }

    @Test(dataProvider = "simpleExpressions")
    public void simpleExpressionsTest(String expression, boolean expectedResult) throws InvalidExpressionException {
        var parsed = ExpressionCompiler.compile(expression, EXPRESSIONS);
        assertThat(parsed.solve()).isEqualTo(expectedResult);
    }

    @DataProvider(name = "invalidExpressions")
    public Object[][] invalidExpressionsProvider() {
        return new Object[][]{
                {"X"},
                {"-"},
                {"0 & 1x"},
                {"1 & & 1"},
                {""},
                {"&"},
        };
    }

    @Test(dataProvider = "invalidExpressions")
    public void invalidExpressionsTest(String expression) {
        assertThatThrownBy(() -> ExpressionCompiler.compile(expression, EXPRESSIONS))
                .isInstanceOf(InvalidExpressionException.class);
    }

    @Test
    public void andExpressionTest() {
        assertThat(new AndExpression(FALSE_EXPRESSION, FALSE_EXPRESSION).solve()).isFalse();
        assertThat(new AndExpression(FALSE_EXPRESSION, TRUE_EXPRESSION).solve()).isFalse();
        assertThat(new AndExpression(TRUE_EXPRESSION, FALSE_EXPRESSION).solve()).isFalse();
        assertThat(new AndExpression(TRUE_EXPRESSION, TRUE_EXPRESSION).solve()).isTrue();
    }

    @Test
    public void orExpressionTest() {
        assertThat(new OrExpression(FALSE_EXPRESSION, FALSE_EXPRESSION).solve()).isFalse();
        assertThat(new OrExpression(FALSE_EXPRESSION, TRUE_EXPRESSION).solve()).isTrue();
        assertThat(new OrExpression(TRUE_EXPRESSION, FALSE_EXPRESSION).solve()).isTrue();
        assertThat(new OrExpression(TRUE_EXPRESSION, TRUE_EXPRESSION).solve()).isTrue();
    }

    @Test
    public void xorExpressionTest() {
        assertThat(new XorExpression(FALSE_EXPRESSION, FALSE_EXPRESSION).solve()).isFalse();
        assertThat(new XorExpression(FALSE_EXPRESSION, TRUE_EXPRESSION).solve()).isTrue();
        assertThat(new XorExpression(TRUE_EXPRESSION, FALSE_EXPRESSION).solve()).isTrue();
        assertThat(new XorExpression(TRUE_EXPRESSION, TRUE_EXPRESSION).solve()).isFalse();
    }

    @Test
    public void notExpressionTest() {
        assertThat(new NotExpression(FALSE_EXPRESSION).solve()).isTrue();
        assertThat(new NotExpression(TRUE_EXPRESSION).solve()).isFalse();
    }

    @Test
    public void predicateExpressionTest() {
        var counter = new AtomicInteger();
        Predicate<Boolean> predicate = (bool) -> {
            counter.incrementAndGet();
            return bool.equals(Boolean.FALSE);
        };
        var expression = new PredicateExpression<>(predicate, Boolean.FALSE);

        assertThat(expression.solve()).isTrue();
        assertThat(counter.get()).isEqualTo(1);
        assertThat(expression.solve()).isTrue();
        assertThat(counter.get()).isEqualTo(1);

        expression.setParam(Boolean.TRUE);

        assertThat(expression.solve()).isFalse();
        assertThat(counter.get()).isEqualTo(2);
        assertThat(expression.solve()).isFalse();
        assertThat(counter.get()).isEqualTo(2);
    }

    @Test
    public void bipredicateExpressionTest() {
        var counter = new AtomicInteger();
        BiPredicate<Boolean, Boolean> predicate = (boolA, boolB) -> {
            counter.incrementAndGet();
            return boolA.equals(boolB);
        };
        var expression = new BiPredicateExpression<>(predicate, Boolean.FALSE, Boolean.FALSE);

        assertThat(expression.solve()).isTrue();
        assertThat(counter.get()).isEqualTo(1);
        assertThat(expression.solve()).isTrue();
        assertThat(counter.get()).isEqualTo(1);

        expression.setParams(Boolean.TRUE, Boolean.FALSE);

        assertThat(expression.solve()).isFalse();
        assertThat(counter.get()).isEqualTo(2);
        assertThat(expression.solve()).isFalse();
        assertThat(counter.get()).isEqualTo(2);
    }
}
