package jvalidations.validations;

import jvalidations.AbstractJValidationsTestCase;
import static jvalidations.validations.TrueValidationTest.*;

public class FalseValidationTest extends AbstractJValidationsTestCase {

    public void testIsFalseReturnsTrueWhenSuppliedObjectIsFalseOrAStringRepresentingFalse() {
        assertAllFalse(check(FalseValidation.isFalse(), TRUE_LIST));
        assertAllTrue(check(FalseValidation.isFalse(), FALSE_LIST));
        assertAllFalse(check(FalseValidation.isFalse(), NEITHER_FALSE_OR_TRUE_LIST));
    }

    public void testIsNotFalseReturnsTrueWhenSuppliedObjectIsNotFalseOrAStringRepresentingFalse() {
        assertAllTrue(check(FalseValidation.isNotFalse(), TRUE_LIST));
        assertAllFalse(check(FalseValidation.isNotFalse(), FALSE_LIST));
        assertAllTrue(check(FalseValidation.isNotFalse(), NEITHER_FALSE_OR_TRUE_LIST));
    }

    public void testHasNoParameters() {
        assertAllNull(parameters(FalseValidation.isFalse(), edgeCaseParameterNames()));
        assertAllNull(parameters(FalseValidation.isNotFalse(), edgeCaseParameterNames()));
    }
}
