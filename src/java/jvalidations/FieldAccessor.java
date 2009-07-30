package jvalidations;

import static jvalidations.functional.Functors.declaredField;
import static jvalidations.functional.Functors.superClass;
import static jvalidations.functional.Functional.find;

import java.lang.reflect.Field;

public class FieldAccessor extends AbstractAccessor {

    public FieldAccessor(String name) {
        super(name);
    }

    public Object value(Object candidate) {
        try {
            Field field = find(declaredField(name), candidate.getClass(), superClass());
            field.setAccessible(true);
            return field.get(candidate);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
