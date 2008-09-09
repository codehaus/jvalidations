package jvalidations.validations;

import static jedi.functional.Coercions.list;
import jvalidations.AbstractJValidationsTestCase;
import static jvalidations.validations.OneOfValidation.isNotOneOf;
import static jvalidations.validations.OneOfValidation.isOneOf;

import java.util.List;

public class OneOfValidationTest extends AbstractJValidationsTestCase {

    public void testIsOneOfReturnsTrueIfTheSuppliedObjectIsOneOfTheAllowedList() {
        assertAllTrue(check(isOneOf(list(null,"a", "b", "c")), list("a", "b", "c",null)));
        assertAllFalse(check(isOneOf(list("d", "e", "f")), list("a", "b", "c",null)));
    }

    public void testIsNotOneOfReturnsTrueIfTheSuppliedObjectIsNotOneOfTheAllowedList() {
        assertAllFalse(check(isNotOneOf(list(null,"a", "b", "c")), list("a", "b", "c",null)));
        assertAllTrue(check(isNotOneOf(list("d", "e", "f")), list("a", "b", "c",null)));
    }

    public void testUnderstandsTheParameterCalledPossibilities() {
        List<String> expected = list("a", "b", "c", null);
        assertEquals(expected, isOneOf(expected).parameter("possibilities"));
        assertEquals(expected, isNotOneOf(expected).parameter("possibilities"));

        assertAllNull(parameters(isOneOf(null), edgeCaseParameterNames()));
        assertAllNull(parameters(isNotOneOf(null), edgeCaseParameterNames()));

    }
}
