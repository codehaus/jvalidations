package jvalidations;

public interface Validatable<R> {
    void buildValidation(ValidationSyntax validates, R report);
}
