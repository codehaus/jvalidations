package jvalidations;

import static jedi.functional.Coercions.list;
import static jedi.functional.FunctionalPrimitives.collect;
import jedi.functional.Coercions;
import static jvalidations.Validation.Functors._check;
import static jvalidations.Validation.Functors._parameter;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import java.util.List;

public abstract class AbstractJValidationsTestCase extends MockObjectTestCase {
    protected MockSelectors mock() {
        return new MockSelectors(this);
    }

    protected void assertAllFalse(List<Boolean> booleans) {
        assertBooleans(FALSE,booleans);
    }

    protected void assertAllTrue(List<Boolean> booleans) {
        assertBooleans(TRUE,booleans);
    }

    private void assertBooleans(Boolean expected,List<Boolean> booleans) {
        int i=1;
        for (Boolean aBoolean : booleans) {
            assertEquals(""+i,expected,aBoolean);
            i++;
        }
    }

    protected List<Boolean> check(final Validation validation) {
        return check(validation, sampleValidatables());
    }

    protected List<Boolean> check(final Validation validation, List<? extends Object> sampleValues) {
        return collect(sampleValues, _check(validation));
    }

    protected List<Object> sampleValidatables() {
        return list(null, "", "  ", new Object());
    }

    protected Validation falseValidation() {
        return validation(false);
    }

    protected Validation trueValidation() {
        return validation(true);
    }

    private Validation validation(final boolean result) {
        return mock().validation().withCheck(result).build();
    }

    protected void assertAllNull(List<Object> objects) {
        int i=1;
        for (Object object : objects) {
            assertNull(""+i,object);
            i++;
        }
    }

    protected List<Object> parameters(Validation validation, List<String> sampleParameterNames) {
        return collect(sampleParameterNames, _parameter(validation));
    }

    protected List<String> edgeCaseParameterNames() {
        return list(null,"","  ", "Ernie");
    }

    protected static class MockSelectors  {
        private final MockObjectTestCase testCase;

        public MockSelectors(MockObjectTestCase testCase) {
            this.testCase = testCase;
        }

        public ValidationLogicTest.MockValidationBuilder validation() {
            return new ValidationLogicTest.MockValidationBuilder(testCase);
        }

        public MockCardinalityBuilder cardinality() {
            return new MockCardinalityBuilder(testCase);
        }
    }

    protected static class MockCardinalityBuilder {
        private final MockObjectTestCase testCase;
        private final Mock mock;

        public MockCardinalityBuilder(MockObjectTestCase testCase) {
            this.testCase = testCase;
            mock = testCase.mock(Cardinality.class);
        }

        public MockCardinalityBuilder withRequiredCount(int i) {
            mock.expects(testCase.atLeastOnce()).method("requiredCount").will(testCase.returnValue(i));
            return this;
        }

        public Cardinality build() {
            return (Cardinality) mock.proxy();
        }

        public MockCardinalityBuilder withAccessors(Accessor ...accessors) {
            mock.expects(testCase.atLeastOnce()).method("getAccessors").will(testCase.returnValue(Coercions.asList(accessors)));
            return this;
        }
    }

    protected static class MockValidationBuilder {
        private final MockObjectTestCase testCase;
        private final Mock mock;

        public MockValidationBuilder(MockObjectTestCase testCase) {
            this.testCase = testCase;
            mock = testCase.mock(Validation.class);
        }

        public ValidationLogicTest.MockValidationBuilder withParameter(String name, Object result) {
            mock.expects(testCase.atLeastOnce()).method("parameter").with(testCase.eq(name)).will(testCase.returnValue(result));
            return this;
        }

        public Validation build() {
            return (Validation) mock.proxy();
        }

        public ValidationLogicTest.MockValidationBuilder withCheck(boolean result) {
            mock.stubs().method("check").withAnyArguments().will(testCase.returnValue(result));
            return this;
        }
    }
}
