package jvalidations.validations;

import static jedi.functional.Coercions.list;
import jvalidations.AbstractJValidationsTestCase;

public class BlankValidationTest extends AbstractJValidationsTestCase {

    public void testIsBlankValidationReturnsTrueWhenSuppliedObjectIsBlank() {
        assertAllTrue(check(BlankValidation.isBlank(), list(""," ","  ")));
        assertAllFalse(check(BlankValidation.isBlank(), list(null,  new Object())));
    }

    public void testIsNotBlankValidationReturnsTrueWhenSuppliedObjectIsNotBlank() {
        assertAllFalse(check(BlankValidation.isNotBlank(), list(null,""," ","  ")));
        assertAllTrue(check(BlankValidation.isNotBlank(), list("xxx", new Object())));
    }

    public void testHasNoParameters() {
        assertAllNull(parameters(BlankValidation.isBlank(), edgeCaseParameterNames()));
        assertAllNull(parameters(BlankValidation.isNotBlank(), edgeCaseParameterNames()));
    }
}