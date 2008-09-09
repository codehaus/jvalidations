package jvalidations;

import jedi.functional.Functor;

public interface Condition {
    boolean check(Object o);

    public class Functors {
        public static Functor<Condition,Boolean> _check(final Object o) {
            return new Functor<Condition, Boolean>() {
                public Boolean execute(Condition condition) {
                    return condition.check(o);
                }
            };
        }
    }
}
