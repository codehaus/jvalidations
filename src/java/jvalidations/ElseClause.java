package jvalidations;

import org.hamcrest.Matcher;

public interface ElseClause {
    //TODO change this to (candidate, cardinality, matcher, []accessorsValid, []accessorsInValid)
    void execute(Object candidate, Cardinality cardinality, Matcher matcher, int numValid);
}
