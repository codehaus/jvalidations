package jvalidations;

import static jedi.functional.Coercions.asList;
import static jedi.functional.Coercions.list;
import static jedi.functional.FunctionalPrimitives.collect;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import java.util.List;

public abstract class AbstractJValidationsTestCase {
    protected MockSelectors mock() {
        return new MockSelectors();
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
            Assert.assertEquals("" + i, expected, aBoolean);
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
            Assert.assertNull("" + i, object);
            i++;
        }
    }

    protected List<String> edgeCaseParameterNames() {
        return list(null, "", "  ", "Ernie");
    }

    protected static class MockSelectors {

        public MockMatcherBuilder matcher() {
            return new MockMatcherBuilder();
        }

        public MockCardinalityBuilder cardinality() {
            return new MockCardinalityBuilder();
        }
    }

    protected static class MockCardinalityBuilder {
        private final Cardinality mock;

        public MockCardinalityBuilder() {
            mock = Mockito.mock(Cardinality.class);
        }

        public MockCardinalityBuilder withRequiredCount(int i) {
            when(mock.requiredCount()).thenReturn(i);
            return this;
        }

        public Cardinality build() {
            return mock;
        }

        public MockCardinalityBuilder withAccessors(Accessor... accessors) {
            when(mock.getAccessors()).thenReturn(asList(accessors));
            return this;
        }
    }

    protected static class MockMatcherBuilder {
        private final Matcher mock;

        public MockMatcherBuilder() {
            mock = Mockito.mock(Matcher.class);
        }

        public Matcher build() {
            return mock;
        }

        public MockMatcherBuilder withCheck(boolean result) {
            when(mock.matches(Mockito.any(Object.class))).thenReturn(result);
            return this;
        }

    }
}
