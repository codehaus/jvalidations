package jvalidations;

import static jedi.functional.Coercions.asList;
import jedi.functional.Filter;
import static jedi.functional.FirstOrderLogic.intersection;
import static jedi.functional.FirstOrderLogic.not;
import jedi.functional.Functor;
import static jvalidations.Cardinality.Functors.nested;
import static jvalidations.Condition.Functors._check;
import static jvalidations.DefaultValidationBuilder.ConditionableCommand.hasOneOfTheseTags;
import static jvalidations.functional.Filters.isFalse;
import static jvalidations.functional.Filters.isTrue;
import static jvalidations.functional.Functional.find;
import static jvalidations.functional.Functional.first;
import static jvalidations.functional.Functional.all;
import static jvalidations.functional.Functors.declaredMethod;
import static jvalidations.functional.Functors.superClass;
import static jvalidations.SyntaxSupport.Cardinalities.exactly;

import static java.lang.Boolean.TRUE;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;

public class DefaultValidationBuilder implements ValidationSyntax {
    private List<ConditionableCommand> commands = new ArrayList<ConditionableCommand>();
    private List<Filter<ConditionableCommand>> tagConditions = new ArrayList<Filter<ConditionableCommand>>();
    private final List<Functor<Cardinality, Cardinality>> nestingFunctors =
            new ArrayList<Functor<Cardinality, Cardinality>>();
    private boolean stopOnFirstFailure = false;

    public DefaultValidationBuilder() {
    }

    private DefaultValidationBuilder(List<Functor<Cardinality, Cardinality>> nestingFunctors,
                                     List<Filter<ConditionableCommand>> tagConditions,
                                     Functor<Cardinality, Cardinality> nextNestingFunctor,
                                     boolean stopOnFirstFailure
    ) {
        this.nestingFunctors.addAll(nestingFunctors);
        this.nestingFunctors.add(nextNestingFunctor);
        this.tagConditions.addAll(tagConditions);
        this.stopOnFirstFailure = stopOnFirstFailure;
    }

    public Conditionable that(final String fieldName, final Matcher matcher, final ElseClause elseClause) {
        return that(exactly(1).of(fieldName), matcher, elseClause);
    }

    public Conditionable that(final Cardinality cardinality, final Matcher matcher, final ElseClause elseClause) {
        Command command = new Command() {
            public boolean execute(Object candidate) {
                int numValid = checkValidity(candidate, cardinality, matcher);
                if (!cardinality.happyWith(numValid)) {
                    elseClause.execute(candidate, getCardinalityInRightNestingContext(cardinality), matcher, numValid);
                    return false;
                }
                return true;
            }

            private int checkValidity(Object candidate, Cardinality cardinality, Matcher validation) {
                List<Accessor> accessors = cardinality.getAccessors();
                int numValid = 0;
                for (int i = 0; i < accessors.size() && cardinality.requiresMoreChecks(numValid, accessors.size() - i); i++) {
                    if (validation.matches(accessors.get(i).value(candidate))) {
                        numValid++;
                    }
                }
                return numValid;
            }            
        };
        return addCommand(command);
    }


    private Cardinality getCardinalityInRightNestingContext(Cardinality cardinality) {
        for (Functor<Cardinality, Cardinality> nestingFunctor : nestingFunctors) {
            cardinality = nestingFunctor.execute(cardinality);
        }
        return cardinality;
    }

    public Conditionable associated(final String accessor, final Object report) {
        Command command = new Command() {
            public boolean execute(Object o) {
                Accessor accessorObj = Accessor.Functors.fromString().execute(accessor);
                Object associated = accessorObj.value(o);
                return associated == null || buildNestedValidation(accessorObj, associated, report).validate(associated);
            }

            private DefaultValidationBuilder buildNestedValidation(Accessor accessorObj, Object associated, Object report) {
                Method method =
                        find(declaredMethod("buildValidation", ValidationSyntax.class, Object.class),
                                associated.getClass(),
                                superClass());
                DefaultValidationBuilder nestedBuilder = nest(nested(accessorObj.name()));
                return callNestedBuildValidationMethod(associated, report, method, nestedBuilder);
            }

            private DefaultValidationBuilder callNestedBuildValidationMethod(Object associated,
                                                                             Object report,
                                                                             Method method,
                                                                             DefaultValidationBuilder nestedBuilder) {
                try {
                    method.invoke(associated, nestedBuilder, report);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
                return nestedBuilder;
            }
            

        };
        return addCommand(command);
    }



    private DefaultValidationBuilder nest(Functor<Cardinality, Cardinality> functor) {
        return new DefaultValidationBuilder(nestingFunctors, tagConditions, functor, stopOnFirstFailure);
    }

    public boolean validate(Object domainObject) {
        if(stopOnFirstFailure) {
            return first(commands, isHappyWith(domainObject), isFalse(), Boolean.TRUE);
        }
        return all(commands, isHappyWith(domainObject), isTrue());
    }

    private Functor<ConditionableCommand, Boolean> isHappyWith(final Object domainObject) {
        return new Functor<ConditionableCommand, Boolean>() {
            public Boolean execute(ConditionableCommand command) {
                return !tagConditionsOk(command) || command.execute(domainObject);
            }
        };
    }

    private Conditionable addCommand(Command command) {
        ConditionableCommand conditionableCommand = new ConditionableCommand(command);
        commands.add(conditionableCommand);
        return conditionableCommand;
    }

    private boolean tagConditionsOk(ConditionableCommand command) {
        return conditionsOk(command, tagConditions);
    }

    private boolean conditionsOk(ConditionableCommand command,
                                 List<Filter<ConditionableCommand>> conditions) {
        return first(conditions, _execute(command), isFalse(), TRUE);
    }

    private Functor<Filter<ConditionableCommand>, Boolean> _execute(final ConditionableCommand command) {
        return new Functor<Filter<ConditionableCommand>, Boolean>() {
            public Boolean execute(Filter<ConditionableCommand> conditionableCommandFilter) {
                return conditionableCommandFilter.execute(command);
            }
        };
    }

    public void removeTags(Object... tagsToRemove) {
        tagConditions.add(not(hasOneOfTheseTags(asList(tagsToRemove))));
    }

    public DefaultValidationBuilder stopOnFirstFailure() {
        stopOnFirstFailure = true;
        return this;
    }

    public static class ConditionableCommand implements Conditionable, Command {
        private final Command command;
        private List<Condition> conditions = new ArrayList<Condition>();
        private List<Object> tags = new ArrayList<Object>();

        public ConditionableCommand(Command command) {
            this.command = command;
        }

        public Conditionable on(Condition condition) {
            conditions.add(condition);
            return this;
        }

        public Conditionable tags(Object... tags) {
            this.tags.addAll(asList(tags));
            return this;
        }

        static Filter<ConditionableCommand> hasOneOfTheseTags(final List<Object> tags) {
            return new Filter<ConditionableCommand>() {
                public Boolean execute(ConditionableCommand conditionableCommand) {
                    return !intersection(conditionableCommand.tags, tags).isEmpty();
                }
            };
        }

        public boolean execute(Object o) {
            return !conditionsOk(o) || command.execute(o);
        }

        private boolean conditionsOk(Object o) {
            return first(conditions, _check(o), isFalse(), TRUE);
        }
    }
}
