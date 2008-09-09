package jvalidations.validations;

import static jedi.functional.Coercions.list;
import jvalidations.AbstractJValidationsTestCase;
import static jvalidations.validations.FormatOfValidation.isNotOfFormat;
import static jvalidations.validations.FormatOfValidation.isOfFormat;

import java.util.List;

public class FormatOfValidationTest extends AbstractJValidationsTestCase {
    private List<String> MATCHES = list("ab","bc");
    private List<Object> NON_MATCHES = list("[]","[b",""," ", null,new Object());

    public void testIsOfFormatReturnsTrueWhenSuppliedValueAsAStringMatchesTheSuppliedRegexp() {
        assertAllTrue(check(isOfFormat("^\\w\\w$"), MATCHES));
        assertAllFalse(check(isOfFormat("^\\w\\w$"), NON_MATCHES));
    }

    public void testIsNotOfFormatReturnsTrueWhenSuppliedValueAsAStringDoesNotMatchTheSuppliedRegexp() {
        assertAllFalse(check(isNotOfFormat("^\\w\\w$"), MATCHES));
        assertAllTrue(check(isNotOfFormat("^\\w\\w$"), NON_MATCHES));
    }

    public void testUnderstandsTheParameterCalledFormat() {
        assertEquals("value", isOfFormat("value").parameter("format"));
        assertEquals("value", isNotOfFormat("value").parameter("format"));


        assertAllNull(parameters(isOfFormat("value"), edgeCaseParameterNames()));
        assertAllNull(parameters(isNotOfFormat("value"), edgeCaseParameterNames()));
    }
}
