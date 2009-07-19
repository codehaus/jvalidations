package jvalidations;

import static jedi.functional.Coercions.list;
import static jedi.functional.FunctionalPrimitives.collect;
import jedi.functional.Coercions;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.hamcrest.Matcher;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import java.util.List;

public abstract class AbstractJValidationsTestCase extends MockObjectTestCase {
    protected MockSelectors mock() {
        return new MockSelectors(this);
    }

    protected void assertAllFalse(List<Boolean> booleans) {
        assertBooleans(FALSE, booleans);
    }

    protected void assertAllTrue(List<Boolean> booleans) {
        assertBooleans(TRUE, booleans);
    }

    private void assertBooleans(Boolean expected, List<Boolean> booleans) {
        int i = 1;
        for (Boolean aBoolean : booleans) {
            assertEquals("" + i, expected, aBoolean);
            i++;
        }
    }

    protected List<Boolean> check(final Matcher matcher) {
        return check(matcher, sampleValidatables());
    }

    protected List<Boolean> check(final Matcher matcher, List sampleValues) {
        return collect(sampleValues, MatcherFunctors._matches(matcher));
    }

    protected List<Object> sampleValidatables() {
        return list(null, "", "  ", new Object());
    }

    protected Matcher falseMatcher() {
        return matcher(false);
    }

    protected Matcher trueMatcher() {
        return matcher(true);
    }

    private Matcher matcher(final boolean result) {
        return mock().matcher().withCheck(result).build();
    }

    protected void assertAllNull(List<Object> objects) {
        int i = 1;
        for (Object object : objects) {
            assertNull("" + i, object);
            i++;
        }
    }

    protected List<String> edgeCaseParameterNames() {
        return list(null, "", "  ", "Ernie");
    }

    protected static class MockSelectors {
        private final MockObjectTestCase testCase;

        public MockSelectors(MockObjectTestCase testCase) {
            this.testCase = testCase;
        }

        public MockMatcherBuilder matcher() {
            return new MockMatcherBuilder(testCase);
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

        public MockCardinalityBuilder withAccessors(Accessor... accessors) {
            mock.expects(testCase.atLeastOnce()).method("getAccessors")
                    .will(testCase.returnValue(Coercions.asList(accessors)));
            return this;
        }
    }

    protected static class MockMatcherBuilder {
        private final MockObjectTestCase testCase;
        private final Mock mock;

        public MockMatcherBuilder(MockObjectTestCase testCase) {
            this.testCase = testCase;
            mock = testCase.mock(Matcher.class);
        }

        public Matcher build() {
            return (Matcher) mock.proxy();
        }

        public MockMatcherBuilder withCheck(boolean result) {
            mock.stubs().method("matches").withAnyArguments().will(testCase.returnValue(result));
            return this;
        }

    }
}
