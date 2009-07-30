package jvalidations;

import org.hamcrest.Matcher;

public interface ValidationSyntax {
    Conditionable that(String accessor, Matcher matcher, ElseClause elseClause);

    Conditionable that(Cardinality cardinality, Matcher matcher, ElseClause elseClause);

    Conditionable associated(String accessor, Object report);
}
