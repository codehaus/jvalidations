package jvalidations;

import jedi.functional.Functor;

public interface ParameterLookupForCallbackMethod {
    public static class Functors {
        public static Functor<ParameterLookupForCallbackMethod, Class> type(final Object candidate, final Cardinality cardinality, final Validation validation) {
            return new Functor<ParameterLookupForCallbackMethod, Class>() {
                public Class execute(ParameterLookupForCallbackMethod parameterLookupForCallbackMethod) {
                    return parameterLookupForCallbackMethod.type(candidate, cardinality, validation);
                }
            };
        }

        public static Functor<ParameterLookupForCallbackMethod, Object> value(final Object candidate, final Cardinality cardinality, final Validation validation, final int numValid) {
            return new Functor<ParameterLookupForCallbackMethod, Object>() {
                public Object execute(ParameterLookupForCallbackMethod parameterLookupForCallbackMethod) {
                    return parameterLookupForCallbackMethod.value(candidate, cardinality, validation, numValid);
                }
            };
        }

    }

    Class type(Object candidate, Cardinality cardinality, Validation validation);

    Object value(Object candidate, Cardinality cardinality, Validation validation, int numValid);
}
