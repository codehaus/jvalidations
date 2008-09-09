package jvalidations;

import jedi.functional.Functor;

public interface Accessor {

    String name();

    Object value(Object candidate);

    public class Functors {
        public static Functor<Accessor, String> name() {
            return new Functor<Accessor, String>() {
                public String execute(Accessor accessor) {
                    return accessor.name();
                }
            };
        }

        public static Functor<Accessor, Accessor> nested(final String name) {
            return new Functor<Accessor, Accessor>() {
                public Accessor execute(final Accessor accessor) {
                    return new Accessor() {
                        public String name() {
                            return (name != null && name.length() > 0) ? name + "." + accessor.name() : accessor.name();
                        }

                        public Object value(Object candidate) {
                            return accessor.value(candidate);
                        }
                    };
                }
            };
        }

        public static Functor<String, Accessor> fromString() {
            return new Functor<String, Accessor>() {
                public Accessor execute(String s) {
                    if(s.endsWith("()")) {
                        return new MethodAccessor(s.substring(0,s.length()-2));
                    }
                    return new FieldAccessor(s);
                }
            };
        }
    }
}
