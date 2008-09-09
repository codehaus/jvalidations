package jvalidations.validations;

import jvalidations.Validation;
import jvalidations.SyntaxSupport;

public class NullValidation {
    public static Validation isNull() {
        return new AbstractParameterlessValidation() {
            public boolean check(Object o) {
                return o == null;
            }
        };
    }

    public static Validation isNotNull() {
        return SyntaxSupport.ValidationLogic.not(isNull());
    }
}
