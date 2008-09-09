package jvalidations;

import static jedi.functional.Coercions.asArray;
import static jedi.functional.FunctionalPrimitives.collect;
import static jvalidations.Accessor.Functors.name;
import static jvalidations.ParameterLookupForCallbackMethod.Functors.type;
import static jvalidations.ParameterLookupForCallbackMethod.Functors.value;
import static jvalidations.Validation.Functors._check;
import static jvalidations.Validation.Functors._parameter;
import static jvalidations.functional.Filters.isFalse;
import static jvalidations.functional.Filters.notNull;
import static jvalidations.functional.Functional.first;
import static jvalidations.functional.Functional.find;
import static jvalidations.functional.Functors.declaredMethod;
import static jvalidations.functional.Functors.superClass;
import static jvalidations.validations.TrueValidation.isTrue;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class SyntaxSupport {
    public static ElseClause _else(final Object report,
                                   final String callbackMethodName,
                                   final ParameterLookupForCallbackMethod... parameterLookupForCallbackMethod
    ) {
        return new ElseClause() {
            public void execute(Object candidate, Cardinality cardinality, Validation validation, int numValid) {
                Class[] types = types(candidate, cardinality, validation, parameterLookupForCallbackMethod);
                Object[] values = parameters(candidate, cardinality, validation, numValid, parameterLookupForCallbackMethod);
                try {
                    Method method = find(declaredMethod(callbackMethodName, types), report.getClass(), superClass());
                    method.invoke(report, values);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    Throwable targetException = e.getTargetException();
                    if(targetException instanceof RuntimeException) {
                        throw (RuntimeException) targetException;
                    }
                    throw new RuntimeException(targetException);
                }
            }

            public Class[] types(Object candidate,
                                 Cardinality cardinality,
                                 Validation validation,
                                 ParameterLookupForCallbackMethod... parameterLookupForCallbackMethod) {
                return parameterLookupForCallbackMethod.length == 0 ? new Class[0] : asArray(
                        collect(parameterLookupForCallbackMethod, type(candidate, cardinality, validation)));
            }

            public Object[] parameters(Object candidate,
                                       Cardinality cardinality,
                                       Validation validation,
                                       int numValid,
                                       ParameterLookupForCallbackMethod... parameterLookupForCallbackMethod) {
                List<Object> list =
                        collect(parameterLookupForCallbackMethod, value(candidate, cardinality, validation, numValid));

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
            return condition(accessorName, isTrue());
        }

        public static Condition condition(final String accessorName, final Validation validation) {
            return new Condition() {
                public boolean check(Object candidate) {
                    Accessor accessor = Accessor.Functors.fromString().execute(accessorName);
                    return validation.check(accessor.value(candidate));
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
            return new AbstractCardinality(){
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
                public Class type(Object candidate, Cardinality cardinality, Validation validation) {
                    return Integer.TYPE;
                }

                public Object value(Object candidate, Cardinality cardinality, Validation validation, int numValid) {
                    return cardinality.requiredCount();
                }
            };
        }

        public static ParameterLookupForCallbackMethod actualCount() {
            return new ParameterLookupForCallbackMethod() {
                public Class type(Object candidate, Cardinality cardinality, Validation validation) {
                    return Integer.TYPE;
                }

                public Object value(Object candidate, Cardinality cardinality, Validation validation, int numValid) {
                    return numValid;
                }
            };
        }

        public static ParameterLookupForCallbackMethod fieldNames() {
            return new ParameterLookupForCallbackMethod() {
                public Class type(Object candidate, Cardinality cardinality, Validation validation) {
                    return new String[0].getClass();
                }

                public Object value(Object candidate,
                                    Cardinality cardinality,
                                    Validation validation,
                                    int numValid) {
                    return asArray(collect(cardinality.getAccessors(), name()));
                }
            };
        }

        public static ParameterLookupForCallbackMethod fieldName() {
            return new ParameterLookupForCallbackMethod() {
                public Class type(Object candidate, Cardinality cardinality, Validation validation) {
                    return String.class;
                }

                public Object value(Object candidate, Cardinality cardinality, Validation validation, int numValid) {
                    return cardinality.getAccessors().get(0).name();
                }
            };
        }
    }

    public static class ValidationLogic {

        public static Validation not(final Validation delegate) {
            return new Validation() {
                public boolean check(Object o) {
                    return !delegate.check(o);
                }

                public Object parameter(String name) {
                    return delegate.parameter(name);
                }
            };
        }

        public static Validation and(final Validation... validations) {
            return new Validation() {
                public boolean check(Object o) {
                    return first(validations, _check(o), isFalse(),
                            TRUE);
                }

                public Object parameter(String name) {
                    return first(validations, _parameter(name), notNull(), null);
                }
            };
        }

        public static Validation or(final Validation... validations) {
            return new Validation() {
                public boolean check(Object o) {
                    return first(validations, _check(o), jvalidations.functional.Filters.isTrue(),
                            FALSE);
                }

                public Object parameter(String name) {
                    return first(validations, _parameter(name), notNull(), null);
                }
            };
        }
    }

}
