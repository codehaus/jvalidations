package jvalidations;

import static jvalidations.functional.Functional.find;
import static jvalidations.functional.Functors.declaredMethod;
import static jvalidations.functional.Functors.superClass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodAccessor extends AbstractAccessor{

    public MethodAccessor(String name) {
        super(name);
    }

    public Object value(Object candidate) {
        try {
            Method method = find(declaredMethod(name), candidate.getClass(), superClass());
            method.setAccessible(true);
            return method.invoke(candidate);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
