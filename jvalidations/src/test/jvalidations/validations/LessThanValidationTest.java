package jvalidations.validations;

import static jedi.functional.Coercions.list;
import jedi.functional.Command;
import static jedi.functional.FunctionalPrimitives.forEach;
import jvalidations.AbstractJValidationsTestCase;
import static jvalidations.validations.LessThanValidation.isLessThan;
import static jvalidations.validations.LessThanValidation.isNotLessThan;

import java.util.List;

public class LessThanValidationTest extends AbstractJValidationsTestCase {
    private List<? extends Number> LESS_THAN_TEN_LIST = list(9, 9l, 9.01f, 9.01d, 9.9999999999d);
    private List<? extends Number> NOT_LESS_THAN_TEN_LIST = list(10, 10l, 10f, 10f, 11, 11l, 10.01f, 10.01d, 10.0000000001d);

    public void testIsLessThanReturnsTrueWhenTheSuppliedTestValueIsLessThanTheSpecifiedLimit() {
        forEach(list(10, 10l, 10f, 10d), new Command<Number>() {
            public void execute(Number n) {
                assertAllTrue(check(isLessThan(n), LESS_THAN_TEN_LIST));
                assertAllFalse(check(isLessThan(n), NOT_LESS_THAN_TEN_LIST));
            }
        });
    }

    public void testIsNotLessThanReturnsTrueWhenTheSuppliedTestValueIsNotLessThanTheSpecifiedLimit() {
        forEach(list(10, 10l, 10f, 10d), new Command<Number>() {
            public void execute(Number n) {
                assertAllFalse(check(isNotLessThan(n), LESS_THAN_TEN_LIST));
                assertAllTrue(check(isNotLessThan(n), NOT_LESS_THAN_TEN_LIST));
            }
        });
    }

    public void testUnderstandsTheParameterCalledLimit() {
        forEach(list(10, 10l, 10f, 10d), new Command<Number>() {
            public void execute(Number n) {
                assertEquals(n, isLessThan(n).parameter("limit"));

                assertEquals(n, isNotLessThan(n).parameter("limit"));

                assertAllNull(parameters(isLessThan(n), edgeCaseParameterNames()));
                assertAllNull(parameters(isNotLessThan(n), edgeCaseParameterNames()));
            }
        });
    }

}