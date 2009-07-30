package jvalidations;

public class JValidations {
    public static <R> void validate(Validatable<R> validatable, R report) {
        DefaultValidationBuilder builder = new DefaultValidationBuilder();
        validatable.buildValidation(builder, report);
        builder.validate(validatable);
    }
}
