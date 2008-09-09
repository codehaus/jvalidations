package jvalidations.validations;

import jvalidations.Validation;
import jvalidations.SyntaxSupport;

import java.util.Collection;

public class OneOfValidation {
    public static Validation isOneOf(final Collection<? extends Object> possibilities) {
        return new AbstractParameterizedValidation() {
            {
                registerParameter("possibilities", possibilities);
            }

            public boolean check(Object value) {
                return possibilities.contains(value);
            }
        };
    }

    public static Validation isNotOneOf(final Collection<? extends Object> possibilities) {
        return SyntaxSupport.ValidationLogic.not(isOneOf(possibilities));
    }
}
