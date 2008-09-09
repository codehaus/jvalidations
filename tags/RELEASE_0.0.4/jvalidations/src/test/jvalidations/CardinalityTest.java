package jvalidations;

import static jedi.functional.Coercions.list;
import static jedi.functional.FunctionalPrimitives.collect;
import static jvalidations.Accessor.Functors.name;

import java.util.List;

public class CardinalityTest extends AbstractJValidationsTestCase {
    
    public void testAtLeastCardinality() {
        Cardinality atLeastTwo = SyntaxSupport.Cardinalities.atLeast(2);

        assertUnhappyWith(atLeastTwo, 0, 1);
        assertHappyWith(atLeastTwo, 2, 3);

        assertRequiresNoMoreChecks(atLeastTwo, list(0,1,2,3), list(0));
        assertRequiresNoMoreChecks(atLeastTwo, list(2,3), list(0,1,2,3));
        assertRequiresMoreChecks(atLeastTwo, list(0,1), list(1,2,3));

        assertEquals(list("a","b"), collect(atLeastTwo.of("a","b").getAccessors(), name()));

        assertEquals(2, atLeastTwo.requiredCount());
    }


    public void testAllCardinality() {
        Cardinality allOfTwo = SyntaxSupport.Cardinalities.all().of("a","b");

        assertUnhappyWith(allOfTwo, 0, 1, 3);
        assertHappyWith(allOfTwo, 2);

        assertRequiresNoMoreChecks(allOfTwo, list(0,1,2), list(0));
        assertRequiresMoreChecks(allOfTwo, list(0,1), list(1,2));

        assertEquals(list("a","b"), collect(allOfTwo.getAccessors(), name()));

        assertEquals(2, allOfTwo.requiredCount());
    }

    public void testExactlyCardinality() {
        Cardinality exactlyTwo = SyntaxSupport.Cardinalities.exactly(2);

        assertUnhappyWith(exactlyTwo, 0, 1, 3);
        assertHappyWith(exactlyTwo, 2);

        assertRequiresNoMoreChecks(exactlyTwo,  list(0,1,2,3), list(0));
        assertRequiresNoMoreChecks(exactlyTwo,  list(3), list(1,2,3));
        assertRequiresMoreChecks(exactlyTwo,  list(0,1,2), list(1,2,3));

        assertEquals(list("a","b","c"), collect(exactlyTwo.of("a","b","c").getAccessors(), name()));

        assertEquals(2, exactlyTwo.requiredCount());
    }

    public void testBothCardinality() {
        Cardinality both = SyntaxSupport.Cardinalities.both().of("a", "b");

        assertUnhappyWith(both, 0, 1, 3);
        assertHappyWith(both, 2);

        assertRequiresNoMoreChecks(both, list(0,1,2), list(0));
        assertRequiresMoreChecks(both, list(0,1), list(1,2));

        assertEquals(list("a","b"), collect(both.getAccessors(), name()));

        assertEquals(2, both.requiredCount());
    }

    public void testAllOrNoneCardinality() {
        Cardinality allOrNoneOfTwo = SyntaxSupport.Cardinalities.allOrNone().of("a","b");
        assertUnhappyWith(allOrNoneOfTwo, 1, 3);
        assertHappyWith(allOrNoneOfTwo, 0,2);

        assertRequiresNoMoreChecks(allOrNoneOfTwo, list(0,1,2), list(0));
        assertRequiresMoreChecks(allOrNoneOfTwo, list(0,1, 2), list(1,2));

        assertEquals(list("a","b"), collect(allOrNoneOfTwo.getAccessors(), name()));
    }

    private void assertRequiresMoreChecks(Cardinality cardinality,
                                            List<Integer> numValids,
                                            List<Integer> numChecksRemainings) {
        doRequiresMoreChecksAssert(cardinality, numValids, numChecksRemainings, true);
    }

    private void assertRequiresNoMoreChecks(Cardinality cardinality,
                                            List<Integer> numValids,
                                            List<Integer> numChecksRemainings) {
        doRequiresMoreChecksAssert(cardinality, numValids, numChecksRemainings, false);
    }

    private void doRequiresMoreChecksAssert(Cardinality cardinality,
                                            List<Integer> numValids,
                                            List<Integer> numChecksRemainings, boolean shouldRequireMoreChecks) {
        for (Integer numValid : numValids) {
            for (Integer numChecksRemaining : numChecksRemainings) {
                assertEquals("" + numValid + ":" + numChecksRemaining, shouldRequireMoreChecks, cardinality.requiresMoreChecks(numValid, numChecksRemaining));
            }
        }
    }

    private void assertUnhappyWith(Cardinality cardinality, int ...values) {
        doHappyWithAsserts(cardinality, false, values);
    }

    private void assertHappyWith(Cardinality cardinality, int ...values) {
        doHappyWithAsserts(cardinality, true, values);
    }

    private void doHappyWithAsserts(Cardinality cardinality, boolean shouldBeHappy, int... values) {
        for (int value : values) {
            assertEquals(""+value, shouldBeHappy, cardinality.happyWith(value));
        }
    }

}
