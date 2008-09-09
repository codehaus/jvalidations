package jvalidations.validations;

import static jedi.functional.Coercions.list;
import jvalidations.Validation;
import jvalidations.SyntaxSupport;

import java.util.Collection;

public class TrueValidation  {
    public static Validation isTrue() {
        return new AbstractTruthValidation() {
            protected Collection<String> allowedValues() {
                return list("true", "yes", "y");
            }
        };
    }

    public static Validation isNotTrue() {
        return SyntaxSupport.ValidationLogic.not(isTrue());
    }
}
