package jvalidations;

import jedi.functional.Functor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static jedi.functional.Coercions.array;
import static jedi.functional.Coercions.asArray;
import static jedi.functional.Coercions.asList;
import static jedi.functional.FunctionalPrimitives.collect;
import static jedi.functional.FunctionalPrimitives.reverse;
import static jedi.functional.FunctionalPrimitives.tail;
import static jvalidations.functional.Functional.find;
import static jvalidations.functional.Functors.declaredMethod;
import static jvalidations.functional.Functors.superClass;

public class MethodAccessor extends AbstractAccessor {

    public MethodAccessor(String name) {
        super(name);
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
        List<String> methodCallOrder = tail(reverse(asList(name.split("\\("))));
        Object []args = new Object[0];
        for (String methodToCall : methodCallOrder) {
            Method method = findMethodOrDie(candidate, methodToCall, methodArgumentTypes(args));
            method.setAccessible(true);
            args = array(method.invoke(candidate, args));
        }
        return args[0];
//        String methodName = name.substring(0, name.length() - 2);
//        Method method = findMethodOrDie(candidate, methodName, new Class[0]);
//        method.setAccessible(true);
//        return method.invoke(candidate, args);
    }

    private Class[] methodArgumentTypes(Object[] args) {
        List<Class> classes = collect(args, getClassFunctor());
        return classes.isEmpty() ? new Class[0] : asArray(classes);
    }

    private Functor<Object, Class> getClassFunctor() {
        return new Functor<Object, Class>() {
            public Class execute(Object o) {
                return o.getClass();
            }
        };
    }

    private Method findMethodOrDie(Object candidate, final String methodName, Class[] methodArgumentTypes) {
        Method method = find(declaredMethod(methodName, methodArgumentTypes), candidate.getClass(), superClass());
        if (method == null) {
            throw new RuntimeException("Could not find method '" + methodName + "' in '" + candidate.getClass() + "'");
        }
        return method;
    }
}
