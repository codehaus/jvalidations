package jvalidations.validations;

import jvalidations.ParameterLookupForCallbackMethod;
import jvalidations.Validation;
import jvalidations.SyntaxSupport;
import static jvalidations.validations.AbstractParameterizedValidation.parameterLookup;

public class EqualsValidation {
    public static Validation isEqualTo(final Object required) {
        return new AbstractParameterizedValidation() {
            {
                registerParameter("required", required);
            }

            public boolean check(Object value) {
                return value == null ? required == null : value.equals(required);
            }
        };
    }

    public static Validation isNotEqualTo(final Object required) {
        return SyntaxSupport.ValidationLogic.not(isEqualTo(required));
    }

    public static ParameterLookupForCallbackMethod required() {
        return parameterLookup("required");
    }
}
