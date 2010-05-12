package jvalidations;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static jvalidations.functional.Functional.find;
import static jvalidations.functional.Functors.declaredMethod;
import static jvalidations.functional.Functors.superClass;

public class MethodAccessor extends AbstractAccessor {

    public MethodAccessor(String name) {
        super(name.substring(0, name.length() - 2));
    }

    public Object value(Object candidate) {
        try {
            return invokeMethod(candidate);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Object invokeMethod(Object candidate) throws IllegalAccessException, InvocationTargetException {
        Method method = findMethodOrDie(candidate);
        method.setAccessible(true);
        return method.invoke(candidate);
    }

    private Method findMethodOrDie(Object candidate) {
        Method method = find(declaredMethod(name), candidate.getClass(), superClass());
        if (method == null) {
            throw new RuntimeException("Could not find method '" + name + "' in '" + candidate.getClass() + "'");
        }
        return method;
    }
}
