package jvalidations.validations;

import jvalidations.Validation;
import jvalidations.SyntaxSupport;

public class GreaterThanValidation extends AbstractSizeValidation {

    private GreaterThanValidation(Number limit) {
        super(limit);
    }

    protected boolean sizeCheckOk(double actual, double limit) {
        return actual > limit;
    }

    public static Validation isGreaterThan(Number n) {
        return new GreaterThanValidation(n);
    }

    public static Validation isNotGreaterThan(Number n) {
        return SyntaxSupport.ValidationLogic.not(isGreaterThan(n));
    }
}