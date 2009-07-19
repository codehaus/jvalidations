package jvalidations;

import jedi.functional.Functor;
import org.hamcrest.Matcher;

public class MatcherFunctors {

    public static Functor<Object, Boolean> _matches(final Matcher matcher) {
        return new Functor<Object, Boolean>() {
            public Boolean execute(Object o) {
                return matcher.matches(o);
            }
        };
    }

    public static Functor<Matcher, Boolean> _matches(final Object object) {
        return new Functor<Matcher, Boolean>() {
            public Boolean execute(Matcher validation) {
                return validation.matches(object);
            }
        };
    }

}
