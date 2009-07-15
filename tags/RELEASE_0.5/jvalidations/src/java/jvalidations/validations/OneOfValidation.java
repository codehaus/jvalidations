package jvalidations.validations;

import jvalidations.Validation;
import jvalidations.SyntaxSupport;
import static jvalidations.SyntaxSupport.ValidationLogic.not;

import java.util.Collection;

public class OneOfValidation {
    public static Validation isOneOf(final Collection possibilities) {
        return new AbstractParameterizedValidation() {
            {
                registerParameter("possibilities", possibilities);
            }

            public boolean check(Object value) {
                return possibilities.contains(value);
            }
        };
    }

    public static Validation isNotOneOf(final Collection possibilities) {
        return not(isOneOf(possibilities));
    }
}
