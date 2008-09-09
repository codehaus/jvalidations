package jvalidations;

import static jvalidations.SyntaxSupport.ValidationLogic;

public class ValidationLogicTest extends AbstractJValidationsTestCase {


    public void testNotNegatesTheResultOfTheSuppliedValidation() {
        assertAllTrue(check(ValidationLogic.not(falseValidation())));
        assertAllFalse(check(ValidationLogic.not(trueValidation())));
    }

    public void testNotDelegatesParameterCallToDelegate() {
        Validation delegate = mock().validation().withParameter("name", "result").build();
        assertEquals("result", ValidationLogic.not(delegate).parameter("name"));
    }

    public void testAndDoesLogicalAndOfSuppliedValidations() {
        assertAllTrue(check( ValidationLogic.and(  trueValidation(),   trueValidation())));
        assertAllFalse(check(ValidationLogic.and(  trueValidation(),   falseValidation())));
        assertAllFalse(check(ValidationLogic.and(  falseValidation(),  trueValidation())));
        assertAllFalse(check(ValidationLogic.and(  falseValidation(),  falseValidation())));
    }

    public void testAndReturnsFirstNonNullParameterFromSuppliedValidations() {
        Validation firstValidation = mock().validation().withParameter("name", null).build();
        Validation secondValidation = mock().validation().withParameter("name", null).build();
        assertNull(ValidationLogic.and(firstValidation, secondValidation).parameter("name"));

        Validation thirdValidation = mock().validation().withParameter("name", "value").build();
        assertEquals("value", ValidationLogic.and(firstValidation, secondValidation, thirdValidation).parameter("name"));
    }

    public void testOrDoesLogicalOrOfSuppliedValidations() {
        assertAllTrue(check( ValidationLogic.or(   trueValidation(),   trueValidation())));
        assertAllTrue(check(ValidationLogic.or(    trueValidation(),   falseValidation())));
        assertAllTrue(check(ValidationLogic.or(    falseValidation(),  trueValidation())));
        assertAllFalse(check(ValidationLogic.or(   falseValidation(),  falseValidation())));
    }

    public void testOrReturnsFirstNonNullParameterFromSuppliedValidations() {
        Validation firstValidation = mock().validation().withParameter("name", null).build();
        Validation secondValidation = mock().validation().withParameter("name", null).build();
        assertNull(ValidationLogic.or(firstValidation, secondValidation).parameter("name"));

        Validation thirdValidation = mock().validation().withParameter("name", "value").build();
        assertEquals("value", ValidationLogic.or(firstValidation, secondValidation, thirdValidation).parameter("name"));
    }


}
