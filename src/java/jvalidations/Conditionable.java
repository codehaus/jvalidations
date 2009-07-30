package jvalidations;

//TODO make an unless()
public interface Conditionable {
    Conditionable on(Condition condition);

    Conditionable tags(Object... tags);
}
