package jvalidations.validations;

import static jvalidations.functional.Functors._length;
import jvalidations.Validation;
import jvalidations.SyntaxSupport;
import static jvalidations.validations.GreaterThanValidation.isGreaterThan;
import static jvalidations.validations.LessThanValidation.isLessThan;

public class LengthOfValidation {
    public static Validation isLongerThan(final int limit) {
        return new DerivedPropertyValidation(_length(), isGreaterThan(limit));
    }

    public static Validation isNotLongerThan(final int limit) {
        return SyntaxSupport.ValidationLogic.not(isLongerThan(limit));
    }

    public static Validation isShorterThan(final int limit) {
        return new DerivedPropertyValidation(_length(), isLessThan(limit));
    }

    public static Validation isNotShorterThan(final int limit) {
        return SyntaxSupport.ValidationLogic.not(isShorterThan(limit));
    }

    public static Validation isNotBetween(final int min, final int max) {
        return SyntaxSupport.ValidationLogic.not(isBetween(min, max));
    }

    public static Validation isBetween(final int min, final int max) {
        DerivedPropertyValidation lowerBound = new DerivedPropertyValidation(_length(), isGreaterThan(min)).aliasParameter("limit", "min");
        DerivedPropertyValidation upperBound = new DerivedPropertyValidation(_length(), isLessThan(max)).aliasParameter("limit", "max");
        return SyntaxSupport.ValidationLogic.and(lowerBound, upperBound);
    }
}
