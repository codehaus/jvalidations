package jvalidations;

public abstract class AbstractAccessor implements Accessor {
    protected String name;

    public AbstractAccessor(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }
}
