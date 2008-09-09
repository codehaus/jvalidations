package jvalidations.validations;

import jvalidations.Validation;
import jvalidations.SyntaxSupport;

public class LessThanValidation extends AbstractSizeValidation {

    private LessThanValidation(Number limit) {
        super(limit);
    }

    protected boolean sizeCheckOk(double actual, double limit) {
        return actual < limit;
    }

    public static Validation isLessThan(Number n) {
        return new LessThanValidation(n);
    }

    public static Validation isNotLessThan(Number n) {
        return SyntaxSupport.ValidationLogic.not(isLessThan(n));
    }
}