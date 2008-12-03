package jvalidations.validations;

import jvalidations.Validation;
import jvalidations.SyntaxSupport;
import static jvalidations.SyntaxSupport.ValidationLogic.not;

public class NullValidation {
    public static Validation isNull() {
        return new AbstractParameterlessValidation() {
            public boolean check(Object o) {
                return o == null;
            }
        };
    }

    public static Validation isNotNull() {
        return not(isNull());
    }
}
