package jvalidations;

import static jedi.functional.FunctionalPrimitives.collect;
import jedi.functional.Functor;

import java.util.List;

public interface Cardinality {
    Cardinality of(String... accessorNames);

    List<Accessor> getAccessors();

    boolean requiresMoreChecks(int numValid, int numberChecksRemaining);

    boolean happyWith(int numValid);

    int requiredCount();

    public static class Functors {
        static Functor<Cardinality, Cardinality> nested(final String nestingName){
            return new Functor<Cardinality, Cardinality>() {
                public Cardinality execute(final Cardinality cardinality) {
                    return new Cardinality() {
                        public Cardinality of(String... accessorNames) {
                            return cardinality.of(accessorNames);
                        }

                        public List<Accessor> getAccessors() {
                            return collect(cardinality.getAccessors(), Accessor.Functors.nested(nestingName));
                        }

                        public boolean requiresMoreChecks(int numValid, int numberChecksRemaining) {
                            return cardinality.requiresMoreChecks(numValid, numberChecksRemaining);
                        }

                        public boolean happyWith(int numValid) {
                            return cardinality.happyWith(numValid);
                        }

                        public int requiredCount() {
                            return cardinality.requiredCount();
                        }
                    };
                }
            };
        }
    }
}
