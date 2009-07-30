package jvalidations.functional;

import jedi.functional.Functor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

public class Functors {
    public static Functor<Class, Class> superClass() {
        return new Functor<Class, Class>() {
            public Class execute(Class aClass) {
                return aClass.getSuperclass();
            }
        };
    }

    public static Functor<Class, Method> declaredMethod(final String name, final Class ...types) {
        return new Functor<Class, Method>() {
            public Method execute(Class aClass) {
                try {
                    return aClass.getDeclaredMethod(name, types);
                } catch (NoSuchMethodException e) {
                    return null;
                }
            }
        };

    }

    public static Functor<Class, Field> declaredField(final String name) {
        return new Functor<Class, Field>() {
            public Field execute(Class aClass) {
                try {
                    return aClass.getDeclaredField(name);
                } catch (NoSuchFieldException e) {
                    return null;
                }
            }
        };
    }

    public static Functor<Object, Integer> _length() {
        return new Functor<Object, Integer>() {
            public Integer execute(Object value) {
                return value == null ? 0 : length(value);
            }

            private Integer length(Object value) {
                if (value.getClass().isArray()) {
                    return ((Object[])value).length;                    
                } else if (value instanceof Collection) {
                    return ((Collection)value).size();
                }
                return value.toString().length();
            }
        };
    }
}
