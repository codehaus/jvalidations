package jvalidations.validations;

import static jedi.functional.Coercions.list;
import jvalidations.AbstractJValidationsTestCase;

public class EqualsValidationTest extends AbstractJValidationsTestCase {

    public void testIsEqualToReturnsTrueWhenBothValuesAreNullOrEqualToEachOther() {
        assertTrue(EqualsValidation.isEqualTo(null).check(null));
        assertAllFalse(check(EqualsValidation.isEqualTo(null), list("", " ", new Object())));

        assertTrue(EqualsValidation.isEqualTo("value").check("value"));
        assertAllFalse(check(EqualsValidation.isEqualTo("value"), list(null, "value2", "", " ", new Object())));
    }

    public void testIsNotEqualToReturnsTrueWhenBothValuesAreNotNullOrNotEqualToEachOther() {
        assertFalse(EqualsValidation.isNotEqualTo(null).check(null));
        assertAllTrue(check(EqualsValidation.isNotEqualTo(null), list("", " ", new Object())));

        assertFalse(EqualsValidation.isNotEqualTo("value").check("value"));
        assertAllTrue(check(EqualsValidation.isNotEqualTo("value"), list(null, "value2", "", " ", new Object())));
    }

    public void testUnderstandsTheParameterCalledRequired() {
        assertEquals("value", EqualsValidation.isEqualTo("value").parameter("required"));
        assertEquals("value", EqualsValidation.isNotEqualTo("value").parameter("required"));

        assertNull(EqualsValidation.isEqualTo(null).parameter("required"));
        assertNull(EqualsValidation.isNotEqualTo(null).parameter("required"));

        assertAllNull(parameters(EqualsValidation.isEqualTo("value"), edgeCaseParameterNames()));
        assertAllNull(parameters(EqualsValidation.isNotEqualTo("value"), edgeCaseParameterNames()));

    }
}
