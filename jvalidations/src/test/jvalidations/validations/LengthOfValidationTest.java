package jvalidations.validations;

import static jedi.functional.Coercions.array;
import static jedi.functional.Coercions.list;
import jvalidations.AbstractJValidationsTestCase;
import static jvalidations.validations.LengthOfValidation.*;

import java.util.List;

public class LengthOfValidationTest extends AbstractJValidationsTestCase {
    private static List<Object> THINGS_OF_LENGTH_FOUR = list("0123", array(0, 1, 2, 3), list(0, 1, 2, 3));
    private static List<Object> THINGS_OF_LENGTH_THREE_OR_LESS =
            list("012", "01", array(0, 1, 2), array(0, 1), list(0, 1, 2), list(0, 1), "", null);


    public void testIsLongerThanReturnsTrueIfSuppliedObjectIsLongerThanSuppliedLimit() {
        assertAllTrue(check(isLongerThan(3), THINGS_OF_LENGTH_FOUR));
        assertAllFalse(check(isLongerThan(3), THINGS_OF_LENGTH_THREE_OR_LESS));
    }

    public void testIsShorterThanReturnsTrueIfSuppliedObjectIsShorterThanSuppliedLimit() {
        assertAllTrue(check(isShorterThan(4), THINGS_OF_LENGTH_THREE_OR_LESS));
        assertAllFalse(check(isShorterThan(4), THINGS_OF_LENGTH_FOUR));
    }

    public void testIsNotLongerThanReturnsTrueIfSuppliedObjectIsNotLongerThanSuppliedLimit() {
        assertAllFalse(check(isNotLongerThan(3), THINGS_OF_LENGTH_FOUR));
        assertAllTrue(check(isNotLongerThan(3), THINGS_OF_LENGTH_THREE_OR_LESS));
    }

    public void testIsNotShorterThanReturnsTrueIfSuppliedObjectIsNotShorterThanSuppliedLimit() {
        assertAllFalse(check(isNotShorterThan(4), THINGS_OF_LENGTH_THREE_OR_LESS));
        assertAllTrue(check(isNotShorterThan(4), THINGS_OF_LENGTH_FOUR));
    }

    public void testIsBetweenReturnsTrueIfSuppliedObjectIsLongerThanTheSuppliedMinAndShorterThanTheSuppliedMax() {
        assertAllTrue(check(isBetween(3,5), THINGS_OF_LENGTH_FOUR));
        assertAllFalse(check(isBetween(3,5), THINGS_OF_LENGTH_THREE_OR_LESS));
    }

    public void testIsNotBetweenReturnsTrueIfSuppliedObjectIsShorterThanTheSuppliedMinOrLongerThanTheSuppliedMax() {
        assertAllFalse(check(isNotBetween(3,5), THINGS_OF_LENGTH_FOUR));
        assertAllTrue(check(isNotBetween(3,5), THINGS_OF_LENGTH_THREE_OR_LESS));
    }

    public void testUnderstandsTheParameterCalledLimit() {
        assertEquals(10, isLongerThan(10).parameter("limit"));
        assertEquals(10, isNotLongerThan(10).parameter("limit"));
        assertEquals(10, isShorterThan(10).parameter("limit"));
        assertEquals(10, isNotShorterThan(10).parameter("limit"));


        assertAllNull(parameters(isLongerThan(10), edgeCaseParameterNames()));
        assertAllNull(parameters(isNotLongerThan(10), edgeCaseParameterNames()));
        assertAllNull(parameters(isShorterThan(10), edgeCaseParameterNames()));
        assertAllNull(parameters(isNotShorterThan(10), edgeCaseParameterNames()));
    }

    public void testIsBetweenAndIsNotBetweenUnderstandTheParametersMinAndMax() {
        assertEquals(3, isBetween(3,5).parameter("min"));
        assertEquals(5, isBetween(3,5).parameter("max"));

        assertEquals(3, isNotBetween(3,5).parameter("min"));
        assertEquals(5, isNotBetween(3,5).parameter("max"));

        assertAllNull(parameters(isBetween(3,5), edgeCaseParameterNames()));
        assertAllNull(parameters(isNotBetween(3,5), edgeCaseParameterNames()));
    }


}

