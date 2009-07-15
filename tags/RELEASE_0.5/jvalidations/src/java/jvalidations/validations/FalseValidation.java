package jvalidations.validations;

import static jedi.functional.Coercions.list;
import jvalidations.Validation;
import jvalidations.SyntaxSupport;

import java.util.Collection;

public class FalseValidation {
    public static Validation isFalse() {
        return new AbstractTruthValidation() {
            protected Collection<String> allowedValues() {
                return list("false", "no", "n");
            }
        };
    }

    public static Validation isNotFalse() {
        return SyntaxSupport.ValidationLogic.not(isFalse());
    }
}