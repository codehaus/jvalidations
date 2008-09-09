package jvalidations;

import jedi.functional.Functor;

public interface Validation {
    boolean check(Object value);

    Object parameter(String name);

    public static class Functors {

        public static Functor<Object, Boolean> _check(final Validation validation) {
            return new Functor<Object, Boolean>() {
                public Boolean execute(Object o) {
                    return validation.check(o);
                }
            };
        }

        public static Functor<Validation, Boolean> _check(final Object object) {
            return new Functor<Validation, Boolean>() {
                public Boolean execute(Validation validation) {
                    return validation.check(object);
                }
            };
        }

        public static Functor<Validation, Object> _parameter(final String name) {
            return new Functor<Validation, Object>() {
                public Object execute(Validation validation) {
                    return validation.parameter(name);
                }
            };
        }

        public static Functor<String, Object> _parameter(final Validation validation) {
            return new Functor<String, Object>() {
                public Object execute(String name) {
                    return validation.parameter(name);
                }
            };
        }
    }
}
