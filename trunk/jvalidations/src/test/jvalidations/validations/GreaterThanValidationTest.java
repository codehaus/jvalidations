package jvalidations.validations;

import static jedi.functional.Coercions.list;
import jedi.functional.Command;
import static jedi.functional.FunctionalPrimitives.forEach;
import jvalidations.AbstractJValidationsTestCase;
import static jvalidations.validations.GreaterThanValidation.isGreaterThan;
import static jvalidations.validations.GreaterThanValidation.isNotGreaterThan;

import java.util.List;

public class GreaterThanValidationTest extends AbstractJValidationsTestCase {
    private List<? extends Number> GREATER_THAN_TEN_LIST = list(11, 12l, 10.01f, 10.01d, 10.00000000001d);
    private List<? extends Number> NOT_GREATER_THAN_TEN_LIST = list(10,10l, 10f, 10f, 9, 9l, 9.01f, 9.01d, 9.9999999999d);

    public void testIsGreaterThanReturnsTrueWhenTheSuppliedTestValueIsGreaterThanTheSpecifiedLimit() {
        forEach(list(10,10l,10f,10d), new Command<Number>() {
            public void execute(Number n) {
                assertAllTrue(check(isGreaterThan(n), GREATER_THAN_TEN_LIST));
                assertAllFalse(check(isGreaterThan(n), NOT_GREATER_THAN_TEN_LIST));
            }
        });
    }

    public void testIsNotGreaterThanReturnsTrueWhenTheSuppliedTestValueIsNotGreaterThanTheSpecifiedLimit() {
        forEach(list(10,10l,10f,10d), new Command<Number>() {
            public void execute(Number n) {
                assertAllFalse(check(isNotGreaterThan(n), GREATER_THAN_TEN_LIST));
                assertAllTrue(check(isNotGreaterThan(n), NOT_GREATER_THAN_TEN_LIST));
            }
        });
    }

    public void testUnderstandsTheParameterCalledLimit() {
        forEach(list(10,10l,10f,10d), new Command<Number>() {
            public void execute(Number n) {
                assertEquals(n, isGreaterThan(n).parameter("limit"));

                assertEquals(n, isNotGreaterThan(n).parameter("limit"));

                assertAllNull(parameters(isGreaterThan(n), edgeCaseParameterNames()));
                assertAllNull(parameters(isNotGreaterThan(n), edgeCaseParameterNames()));
            }
        });
    }
}
