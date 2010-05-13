package jvalidations;

import org.junit.Test;

import java.util.ArrayList;

import static jedi.functional.Coercions.array;
import static jedi.functional.Coercions.asList;
import static junit.framework.Assert.assertSame;
import static jvalidations.SyntaxSupport.Parameters.validatee;
import static org.hamcrest.core.DescribedAs.describedAs;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertEquals;

public class ParametersTest extends AbstractJValidationsTestCase {

    @Test
    public void testRequiredCountParameter() {
        ParameterLookupForCallbackMethod requiredCount = SyntaxSupport.Parameters.requiredCount();
        assertEquals(2, requiredCount.value(null, mock().cardinality().withRequiredCount(2).build(), null, 0));
        assertEquals(Integer.TYPE, requiredCount.type(null, null, null));
    }

    @Test
    public void testActualCountParameter() {
        ParameterLookupForCallbackMethod actual = SyntaxSupport.Parameters.actualCount();
        assertEquals(2, actual.value(null, null, null, 2));
        assertEquals(Integer.TYPE, actual.type(null, null, null));
    }

    @Test
    public void testFieldNamesParameter() {
        ParameterLookupForCallbackMethod fieldNames = SyntaxSupport.Parameters.fieldNames();
        String[] expectedFieldNames = array("a", "b");
        String[] actualFieldNames = (String[]) fieldNames.value(null,
                mock().cardinality().withAccessors(new FieldAccessor("a"), new FieldAccessor("b")).build(), null, 0);
        assertEquals(asList(expectedFieldNames), asList(actualFieldNames));
        assertEquals(expectedFieldNames.getClass(), fieldNames.type(null, null, null));
    }

    @Test
    public void testValidateeParameter() {
        ArrayList objectBeingValidated = new ArrayList();
        Object objectReturnedAsParameter = validatee().value(objectBeingValidated, null, null, 0);
        assertSame(objectBeingValidated, objectReturnedAsParameter);
        assertEquals(ArrayList.class, validatee().type(objectBeingValidated, null, null));
    }

    @Test
    public void testFieldNameParameter() {
        ParameterLookupForCallbackMethod fieldName = SyntaxSupport.Parameters.fieldName();
        String actualFieldName = (String) fieldName.value(null,
                mock().cardinality().withAccessors(new FieldAccessor("a")).build(), null, 0);
        assertEquals("a", actualFieldName);
        assertEquals(String.class, fieldName.type(null, null, null));
    }

    @Test
    public void testQueryMethodsReturnedAsFieldsHaveParenthesisStripped() {
        ParameterLookupForCallbackMethod fieldName = SyntaxSupport.Parameters.fieldName();
        String actualFieldName = (String) fieldName.value(null,
                mock().cardinality().withAccessors(new MethodAccessor("a()")).build(), null, 0);
        assertEquals(String.class, fieldName.type(null, null, null));
        assertEquals("a", actualFieldName);
    }

    @Test
    public void testValidationFailureDescriptionParameter() {
        ParameterLookupForCallbackMethod description = SyntaxSupport.Parameters.failureDescription();
        String actualFieldName = (String) description.value(null,
                null, describedAs("Failure Description", nullValue()), 0);
        assertEquals("Failure Description", actualFieldName);
        assertEquals(String.class, description.type(null, null, null));

    }
}