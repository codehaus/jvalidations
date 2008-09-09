package jvalidations;

public interface ElseClause {
    //TODO change this to (candidate, cardinality, validation, []accessorsValid, []accessorsInValid)
    void execute(Object candidate, Cardinality cardinality, Validation validation, int numValid);
}
