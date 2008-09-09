package jvalidations.validations;

import static jedi.functional.Coercions.list;
import jvalidations.AbstractJValidationsTestCase;

public class NullValidationTest extends AbstractJValidationsTestCase {

    public void testIsNullValidationReturnsTrueWhenSuppliedObjectIsNull() {
        assertTrue(NullValidation.isNull().check(null));
        assertAllFalse(check(NullValidation.isNull(), list("", "  ", new Object())));
    }

    public void testIsNotNullValidationReturnsTrueWhenSuppliedObjectIsNotNull() {
        assertFalse(NullValidation.isNotNull().check(null));
        assertAllTrue(check(NullValidation.isNotNull(), list("", "  ", new Object())));
    }

    public void testHasNoParameters() {
        assertAllNull(parameters(NullValidation.isNull(), edgeCaseParameterNames()));
        assertAllNull(parameters(NullValidation.isNotNull(), edgeCaseParameterNames()));
    }

}
