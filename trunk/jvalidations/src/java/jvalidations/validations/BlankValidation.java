package jvalidations.validations;

import jvalidations.Validation;
import jvalidations.SyntaxSupport;
import static jvalidations.validations.NullValidation.isNotNull;
import static jvalidations.SyntaxSupport.ValidationLogic.not;
import static jvalidations.SyntaxSupport.ValidationLogic.and;

public class BlankValidation {
    public static Validation isNotBlank() {
        return and(isNotNull(),not(isBlank()));
    }

    public static Validation isBlank() {
        return new AbstractParameterlessValidation() {
            public boolean check(Object o) {
                return o!=null && o.toString().trim().length()==0;
            }
        };
    }
}
