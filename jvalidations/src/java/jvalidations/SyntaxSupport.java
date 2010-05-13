package jvalidations;

import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static jedi.functional.Coercions.asArray;
import static jedi.functional.FunctionalPrimitives.collect;
import static jvalidations.Accessor.Functors.name;
import static jvalidations.ParameterLookupForCallbackMethod.Functors.type;
import static jvalidations.ParameterLookupForCallbackMethod.Functors.value;
import static jvalidations.functional.Functional.find;
import static jvalidations.functional.Functors.declaredMethod;
import static jvalidations.functional.Functors.superClass;
import static org.hamcrest.core.Is.is;

public class SyntaxSupport {
    public static ElseClause _else(final Object report,
                                   final String callbackMethodName,
                                   final ParameterLookupForCallbackMethod... parameterLookupForCallbackMethod
    ) {
        return new ElseClause() {
            public void execute(Object candidate, Cardinality cardinality, Matcher matcher, int numValid) {
                try {
                    executeWithoutExceptionHandling(candidate, cardinality, matcher, numValid);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    Throwable targetException = e.getTargetException();
                    if (targetException instanceof RuntimeException) {
                        throw (RuntimeException) targetException;
                    }
                    throw new RuntimeException(targetException);
                }
            }

            private void executeWithoutExceptionHandling(Object candidate,
                                                         Cardinality cardinality,
                                                         Matcher matcher,
                                                         int numValid)
                    throws IllegalAccessException, InvocationTargetException {
                Class[] types = types(candidate, cardinality, matcher, parameterLookupForCallbackMethod);
                Object[] values = parameters(candidate, cardinality, matcher, numValid, parameterLookupForCallbackMethod);
                Method method = find(declaredMethod(callbackMethodName, types), report.getClass(), superClass());
                if (method == null) {
                    throw new RuntimeException(
                            "Could not find method '" + callbackMethodName + "' in '" + report.getClass() + "'");
                }
                method.invoke(report, values);
            }

            public Class[] types(Object candidate,
                                 Cardinality cardinality,
                                 Matcher matcher,
                                 ParameterLookupForCallbackMethod... parameterLookupForCallbackMethod) {
                return parameterLookupForCallbackMethod.length == 0 ? new Class[0] : asArray(
                        collect(parameterLookupForCallbackMethod, type(candidate, cardinality, matcher)));
            }

            public Object[] parameters(Object candidate,
                                       Cardinality cardinality,
                                       Matcher matcher,
                                       int numValid,
                                       ParameterLookupForCallbackMethod... parameterLookupForCallbackMethod) {
                List<Object> list =
                        collect(parameterLookupForCallbackMethod, value(candidate, cardinality, matcher, numValid));

                Object[] values = new Object[list.size()];
                int index = 0;
                while (index < values.length) {
                    values[index] = list.get(index);
                    index++;
                }
                return values;
            }

        };
    }

    public static class Conditions {

        public static Condition condition(final String accessorName) {
            return condition(accessorName, is(true));
        }

        public static Condition condition(final String accessorName, final Matcher matcher) {
            return new Condition() {
                public boolean check(Object candidate) {
                    Accessor accessor = Accessor.Functors.fromString().execute(accessorName);
                    return matcher.matches(accessor.value(candidate));
                }
            };
        }
    }

    public static class Cardinalities {
        private static abstract class AbstractCardinality implements Cardinality {
            protected String[] accessorNames;

            public Cardinality of(String... accessorNames) {
                this.accessorNames = accessorNames;
                return this;
            }

            public List<Accessor> getAccessors() {
                return collect(accessorNames, Accessor.Functors.fromString());
            }
        }

        public static Cardinality atLeast(final int minimum) {
            return new AbstractCardinality() {
                public boolean requiresMoreChecks(int numValid, int numberChecksRemaining) {
                    return numberChecksRemaining > 0 && !happyWith(numValid);
                }

                public boolean happyWith(int numValid) {
                    return numValid >= minimum;
                }

                public int requiredCount() {
                    return minimum;
                }
            };
        }

        public static Cardinality both() {
            return exactly(2);
        }

        public static Cardinality all() {
            return new AbstractCardinality() {
                public boolean requiresMoreChecks(int numValid, int numberChecksRemaining) {
                    return numberChecksRemaining > 0 && numValid != accessorNames.length;
                }

                public boolean happyWith(int numValid) {
                    return numValid == accessorNames.length;
                }

                public int requiredCount() {
                    return accessorNames.length;
                }
            };
        }

        public static Cardinality exactly(final int required) {
            return new AbstractCardinality() {
                public boolean requiresMoreChecks(int numValid, int numberChecksRemaining) {
                    return numberChecksRemaining > 0 && numValid <= required;
                }

                public boolean happyWith(int numValid) {
                    return numValid == required;
                }

                public int requiredCount() {
                    return required;
                }

            };
        }

        public static Cardinality allOrNone() {
            return new AbstractCardinality() {
                public boolean requiresMoreChecks(int numValid, int numberChecksRemaining) {
                    return numberChecksRemaining > 0;
                }

                public boolean happyWith(int numValid) {
                    return numValid == 0 || numValid == accessorNames.length;
                }

                public int requiredCount() {
                    throw new UnsupportedOperationException("This has two possible counts");
                }
            };
        }
    }

    public static class Parameters {

        public static ParameterLookupForCallbackMethod requiredCount() {
            return new ParameterLookupForCallbackMethod() {
                public Class type(Object candidate, Cardinality cardinality, Matcher matcher) {
                    return Integer.TYPE;
                }

                public Object value(Object candidate, Cardinality cardinality, Matcher matcher, int numValid) {
                    return cardinality.requiredCount();
                }
            };
        }

        public static ParameterLookupForCallbackMethod actualCount() {
            return new ParameterLookupForCallbackMethod() {
                public Class type(Object candidate, Cardinality cardinality, Matcher matcher) {
                    return Integer.TYPE;
                }

                public Object value(Object candidate, Cardinality cardinality, Matcher matcher, int numValid) {
                    return numValid;
                }
            };
        }

        public static ParameterLookupForCallbackMethod fieldNames() {
            return new ParameterLookupForCallbackMethod() {
                public Class type(Object candidate, Cardinality cardinality, Matcher matcher) {
                    return new String[0].getClass();
                }

                public Object value(Object candidate,
                                    Cardinality cardinality,
                                    Matcher matcher,
                                    int numValid) {
                    return asArray(collect(cardinality.getAccessors(), name()));
                }
            };
        }

        public static ParameterLookupForCallbackMethod fieldName() {
            return new ParameterLookupForCallbackMethod() {
                public Class type(Object candidate, Cardinality cardinality, Matcher matcher) {
                    return String.class;
                }

                public Object value(Object candidate, Cardinality cardinality, Matcher matcher, int numValid) {
                    String name = cardinality.getAccessors().get(0).name();
                    if (isQueryMethod(name)) {
                        name = stripParenthesis(name);
                    }
                    return name;
                }

                private String stripParenthesis(String name) {
                    return name.substring(0, name.length() - 2);
                }
            };
        }

        private static boolean isQueryMethod(String name) {
            return name.endsWith("()");
        }

        public static ParameterLookupForCallbackMethod constant(final Object value) {
            return new ParameterLookupForCallbackMethod() {
                public Class type(Object candidate, Cardinality cardinality, Matcher matcher) {
                    return value.getClass();
                }

                public Object value(Object candidate, Cardinality cardinality, Matcher matcher, int numValid) {
                    return value;
                }
            };
        }

        public static ParameterLookupForCallbackMethod failureDescription() {
            return new ParameterLookupForCallbackMethod() {
                public Class type(Object candidate, Cardinality cardinality, Matcher matcher) {
                    return String.class;
                }

                public Object value(Object candidate, Cardinality cardinality, Matcher matcher, int numValid) {
                    StringDescription description = new StringDescription();
                    matcher.describeTo(description);
                    return description.toString();
                }
            };
        }

        public static ParameterLookupForCallbackMethod validatee() {
            return new ParameterLookupForCallbackMethod() {
                public Class type(Object candidate, Cardinality cardinality, Matcher matcher) {
                    return candidate.getClass();
                }

                public Object value(Object candidate, Cardinality cardinality, Matcher matcher, int numValid) {
                    return candidate;
                }
            };
        }
    }
}
