package jvalidations.validations;

import jvalidations.ParameterLookupForCallbackMethod;
import jvalidations.Validation;
import jvalidations.SyntaxSupport;
import static jvalidations.validations.AbstractParameterizedValidation.parameterLookup;

public class FormatOfValidation {
    public static Validation isOfFormat(final String format) {
        return new AbstractParameterizedValidation() {
            {
                registerParameter("format",format);
            }

            public boolean check(Object value) {
                return value != null && value.toString().matches(format);
            }
        };
    }

    public static Validation isNotOfFormat(final String format) {
        return SyntaxSupport.ValidationLogic.not(isOfFormat(format));
    }

    public static ParameterLookupForCallbackMethod format() {
        return parameterLookup("format");
    }
}
