package jvalidations;

import static jvalidations.SyntaxSupport.Parameters.fieldName;
import static jvalidations.SyntaxSupport.Parameters.requiredCount;
import static jvalidations.SyntaxSupport.Parameters.actualCount;
import static jvalidations.SyntaxSupport._else;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.*;
import org.junit.Assert;

public class SyntaxSupportTest extends AbstractJValidationsTestCase {

    public void test_elseClauseCallsTheRightMethodWithTheRightParameters() {
        int requiredCount = 4;
        int actualCount = 3;
        String fieldName = "fieldName";
        Callback callback = Mockito.mock(Callback.class);


        Cardinality cardinality = mock().cardinality().withAccessors(new FieldAccessor(fieldName))
                .withRequiredCount(requiredCount).build();

        ElseClause elseClause = _else(callback, "callbackMethod", fieldName(), requiredCount(), actualCount());
        elseClause.execute(null, cardinality, null, actualCount);

        verify(callback).callbackMethod(fieldName,requiredCount, actualCount);
    }

    public void test_elseClauseThrowsRuntimeExceptionsDirectly() {
        Callback callback = Mockito.mock(Callback.class);
        RuntimeException deliberate = new RuntimeException("Deliberate");
        doThrow(deliberate).when(callback).callbackMethod(anyString(), anyInt(), anyInt());

        ElseClause elseClause = _else(callback, "callbackMethod", fieldName(), requiredCount(), actualCount());

        Cardinality cardinality = mock().cardinality().withAccessors(new FieldAccessor(""))
                .withRequiredCount(1).build();
        try {
            elseClause.execute(null, cardinality, null, 1);
            Assert.fail("Did not get expected exception");
        } catch (RuntimeException e) {
            Assert.assertSame(deliberate, e);
        }
    }

    public void test_elseFindsMethodsInSuperClass() {
        ExtendsCallbackForTests callback = new ExtendsCallbackForTests();
        ElseClause elseClause = _else(callback, "callbackMethod", fieldName(), requiredCount(), actualCount());

        Cardinality cardinality = mock().cardinality().withAccessors(new FieldAccessor(""))
                .withRequiredCount(1).build();
        elseClause.execute(null, cardinality, null, 1);
        Assert.assertTrue(callback.wasCalled());
    }

    public void test_elseThrowsAnInformativeExceptionIfMethodNotFound() {
        ElseClause clause = _else(new Object(), "this method does not exist");
        try {
            clause.execute(new Object(), null,null,0);
            Assert.fail("Did not throw nice exception");
        } catch (RuntimeException e) {
            Assert.assertEquals("Could not find method 'this method does not exist' in '" + Object.class +"'", e.getMessage());
        }
    }

    public void testCanReturnConstantsAsParameters() {
        String value = "This is a constant constant";
        ParameterLookupForCallbackMethod param = SyntaxSupport.Parameters.constant(value);
        assertEquals(String.class, param.type(null,null,null));
        assertEquals(value, param.value(null,null,null,0));
    }

    public static interface Callback {
        void callbackMethod(String fieldName, int requiredCount, int actualCount);
    }

    public static class CallbackForTests implements Callback {
        private boolean wasCalled;

        public void callbackMethod(String fieldName, int requiredCount, int actualCount) {
            wasCalled=true;
        }

        public boolean wasCalled() {
            return wasCalled;
        }
    }

    public static class ExtendsCallbackForTests extends CallbackForTests {

    }
}
