package jvalidations;

import jedi.functional.Functor;
import org.hamcrest.Matcher;

public interface ParameterLookupForCallbackMethod {
    public static class Functors {
        public static Functor<ParameterLookupForCallbackMethod, Class> type(final Object candidate, final Cardinality cardinality, final Matcher matcher) {
            return new Functor<ParameterLookupForCallbackMethod, Class>() {
                public Class execute(ParameterLookupForCallbackMethod parameterLookupForCallbackMethod) {
                    return parameterLookupForCallbackMethod.type(candidate, cardinality, matcher);
                }
            };
        }

        public static Functor<ParameterLookupForCallbackMethod, Object> value(final Object candidate, final Cardinality cardinality, final Matcher matcher, final int numValid) {
            return new Functor<ParameterLookupForCallbackMethod, Object>() {
                public Object execute(ParameterLookupForCallbackMethod parameterLookupForCallbackMethod) {
                    return parameterLookupForCallbackMethod.value(candidate, cardinality, matcher, numValid);
                }
            };
        }

    }

    Class type(Object candidate, Cardinality cardinality, Matcher matcher);

    Object value(Object candidate, Cardinality cardinality, Matcher matcher, int numValid);
}
