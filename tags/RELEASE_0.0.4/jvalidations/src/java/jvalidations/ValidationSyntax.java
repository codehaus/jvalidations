package jvalidations;

public interface ValidationSyntax {
    Conditionable that(String accessor, Validation validation, ElseClause elseClause);

    Conditionable that(Cardinality cardinality, Validation validation, ElseClause elseClause);

    Conditionable associated(String accessor, Object report);
}
