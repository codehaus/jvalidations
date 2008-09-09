package jvalidations;

import static jvalidations.SyntaxSupport.Parameters.fieldName;
import static jvalidations.SyntaxSupport.Parameters.requiredCount;
import static jvalidations.SyntaxSupport.Parameters.actualCount;
import static jvalidations.SyntaxSupport._else;
import org.jmock.Mock;

public class SyntaxSupportTest extends AbstractJValidationsTestCase {

    public void test_elseClauseCallsTheRightMethodWithTheRightParameters() {
        int requiredCount = 4;
        int actualCount = 3;
        String fieldName = "fieldName";
        Mock mockCallback = mock(Callback.class);
        mockCallback.expects(once()).method("callbackMethod").with(eq(fieldName), eq(requiredCount), eq(actualCount));

        ElseClause elseClause = _else(mockCallback.proxy(), "callbackMethod", fieldName(), requiredCount(), actualCount());

        Cardinality cardinality = mock().cardinality().withAccessors(new FieldAccessor(fieldName))
                .withRequiredCount(requiredCount).build();
        elseClause.execute(null, cardinality, null, actualCount);
    }

    public void test_elseClauseThrowsRuntimeExceptionsDirectly() {
        Mock mockCallback = mock(Callback.class);
        RuntimeException deliberate = new RuntimeException("Deliberate");
        mockCallback.expects(once()).method("callbackMethod").withAnyArguments().will(throwException(deliberate));

        ElseClause elseClause = _else(mockCallback.proxy(), "callbackMethod", fieldName(), requiredCount(), actualCount());

        Cardinality cardinality = mock().cardinality().withAccessors(new FieldAccessor(""))
                .withRequiredCount(1).build();
        try {
            elseClause.execute(null, cardinality, null, 1);
            fail("Did not get expected exception");
        } catch (RuntimeException e) {
            assertSame(deliberate, e);
        }
    }

    public void test_elseFindsMethodsInSuperClass() {
        ExtendsCallbackForTest callback = new ExtendsCallbackForTest();
        ElseClause elseClause = _else(callback, "callbackMethod", fieldName(), requiredCount(), actualCount());

        Cardinality cardinality = mock().cardinality().withAccessors(new FieldAccessor(""))
                .withRequiredCount(1).build();
        elseClause.execute(null, cardinality, null, 1);
        assertTrue(callback.wasCalled());
    }


    public static interface Callback {
        void callbackMethod(String fieldName, int requiredCount, int actualCount);
    }

    public static class CallbackForTest implements Callback {
        private boolean wasCalled;

        public void callbackMethod(String fieldName, int requiredCount, int actualCount) {
            wasCalled=true;
        }

        public boolean wasCalled() {
            return wasCalled;
        }
    }

    public static class ExtendsCallbackForTest extends CallbackForTest {

    }
}
