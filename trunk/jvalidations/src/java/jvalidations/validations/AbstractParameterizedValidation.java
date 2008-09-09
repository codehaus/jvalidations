package jvalidations.validations;

import jvalidations.Cardinality;
import jvalidations.ParameterLookupForCallbackMethod;
import jvalidations.Validation;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractParameterizedValidation implements Validation {
    private final Map<String, Object> parameters = new HashMap<String, Object>();

    protected void registerParameter(String name, Object value) {
        parameters.put(name,value);
    }

    public Object parameter(String name) {
        return parameters.get(name);
    }

    public static ParameterLookupForCallbackMethod parameterLookup(final String name) {
        return new ParameterLookupForCallbackMethod() {
            public Class type(Object candidate, Cardinality cardinality, Validation validation) {
                return validation.parameter(name).getClass();
            }

            public Object value(Object candidate,
                                Cardinality cardinality,
                                Validation validation,
                                int numValid) {
                return validation.parameter(name);
            }
        };
    }

}