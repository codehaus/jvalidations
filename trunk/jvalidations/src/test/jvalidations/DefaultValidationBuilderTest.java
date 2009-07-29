package jvalidations;

import junit.framework.TestCase;
import static jvalidations.SyntaxSupport.Cardinalities.atLeast;
import static jvalidations.SyntaxSupport.Conditions.condition;
import static jvalidations.SyntaxSupport.Parameters.fieldName;
import static jvalidations.SyntaxSupport._else;
import org.hamcrest.Matcher;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class DefaultValidationBuilderTest extends TestCase {

    public void testDoesNothingWhenValidationSucceeds() {
        DomainObject domainObject = new DomainObject<ValidationReport>("mike", "mike@email.com") {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that("name", willSucceedValidation("mike"), _else(report, "unexpectedCallback"));
                validates.that("email", willSucceedValidation("mike@email.com"), _else(report, "unexpectedCallback"));
            }
        };

        final ValidationReport report = mock(ValidationReport.class);
        doValidation(domainObject, report);
        verifyZeroInteractions(report);
    }

    public void testCanPassValidationParametersToTheCallbackWhenValidationFails() {
        DomainObject domainObject = new DomainObject<ValidationReport>("mike", null) {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                final Matcher matcher = willFailValidation("mike");
                validates.that("name", matcher,
                        _else(report, "expectedCallback", parameterLookup(matcher, "expected value")));
            }
        };

        final ValidationReport report = mock(ValidationReport.class);
        doValidation(domainObject, report);
        verify(report).expectedCallback("expected value");
    }

    public void testCanMakeACallbackForAllFailedValidations() {
        DomainObject domainObject = new DomainObject<ValidationReport>("mike", "mike@email.com") {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that("name", willFailValidation("mike"), _else(report, "expectedCallback"));
                validates.that("email", willFailValidation("mike@email.com"), _else(report, "anotherExpectedCallback"));
            }
        };

        final ValidationReport report = mock(ValidationReport.class);
        doValidation(domainObject, report);
        verify(report).expectedCallback();
        verify(report).anotherExpectedCallback();
    }

    public void testCanStateCardinalityForValidationRules() {
        DomainObject domainObject = new DomainObject<ValidationReport>("mike", "mike@email.com") {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that(atLeast(1).of("name", "email"), willFailValidation("mike", "mike@email.com"),
                        _else(report, "expectedCallback"));
            }
        };
        final ValidationReport report = mock(ValidationReport.class);
        doValidation(domainObject, report);
        verify(report).expectedCallback();
    }

    public void testCanApplyConditionsToValidationRules() {
        DomainObject domainObject = new DomainObject<ValidationReport>("mike", null) {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that("name", willFailValidation("mike"), _else(report, "expectedCallback")).on(willPassCondition());
            }
        };
        ValidationReport report = mock(ValidationReport.class);
        doValidation(domainObject, report);
        verify(report).expectedCallback();

        domainObject = new DomainObject<ValidationReport>("mike", "mike@email.com") {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that("name", willFailValidation(), _else(report, "expectedCallback")).on(willFailCondition());
            }
        };
        report = mock(ValidationReport.class);
        doValidation(domainObject, report);
        verifyZeroInteractions(report);
    }

    public void testCanTagValidationRulesAndExcludeThemDuringValidation() {
        DomainObject domainObject = new DomainObject<ValidationReport>("mike", null) {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that("name", willFailValidation("mike"), _else(report, "expectedCallback")).tags("some tag");
            }
        };
        ValidationReport report = mock(ValidationReport.class);
        doValidation(domainObject, report);
        verify(report).expectedCallback();

        domainObject = new DomainObject<ValidationReport>("mike", null) {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that("name", willFailValidation(), _else(report, "expectedCallback")).tags("some tag");
            }
        };
        report = mock(ValidationReport.class);
        doValidation(domainObject, report, "some tag");
        verifyZeroInteractions(report);

    }

    public void testRemovalOfTagsWorksForNestedObjects() {
        NestedDomainObject innermostNested = new NestedDomainObject<ValidationReport>("over there") {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that("location", null, _else(report, "expectedCallback", fieldName())).tags("tag");
            }
        };

        NestedDomainObject outermostNested = new NestedDomainObject<ValidationReport>(innermostNested) {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that("location", null, _else(report, "expectedCallback", fieldName())).tags("tag");
                validates.associated("nested", report);
            }
        };
        DomainObject domainObject = new DomainObject<ValidationReport>(outermostNested) {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.associated("nested", report);
            }
        };
        ValidationReport report = mock(ValidationReport.class);
        doValidation(domainObject, report, "tag");
        verifyZeroInteractions(report);
    }

    public void testStopOnFirstFailureDoesNotCountFirstTagRemovalAsFailure() {
        DomainObject domainObject = new DomainObject<ValidationReport>("mike", "email") {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that(atLeast(1).of("name"), null, _else(report, "unexpectedCallback")).on(condition("name",
                        notNullValue())).tags("some tag");
                validates.that("email", willFailValidation("email"), _else(report, "expectedCallback",
                        fieldName()));
            }
        };
        DefaultValidationBuilder builder = new DefaultValidationBuilder();
        ValidationReport mockReport = mock(ValidationReport.class);
        domainObject.buildValidation(builder, mockReport);
        builder.removeTags("some tag");
        builder.stopOnFirstFailure();
        builder.validate(domainObject);
        verify(mockReport).expectedCallback("email");
    }

    public void testCanMixConditionsAndTags() {
        DomainObject domainObject = new DomainObject<ValidationReport>("mike", null) {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that("name", willFailValidation(), _else(report, "expectedCallback")).on(willFailCondition())
                        .tags("some tag");
            }
        };
        ValidationReport report = mock(ValidationReport.class);
        doValidation(domainObject, report, "some tag");
        doValidation(domainObject, report);
        verifyZeroInteractions(report);
    }


    public void testAccessorsCanBeMethods() {
        DomainObject domainObject = new DomainObject<ValidationReport>(24) {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that("aMethodToGetAge()", willFailValidation(24), _else(report, "expectedCallback"));
            }
        };
        ValidationReport report = mock(ValidationReport.class);
        doValidation(domainObject, report);
        verify(report).expectedCallback();
    }

    public void testCanValidateAssociatedObjects() {
        NestedDomainObject innermostNested = new NestedDomainObject<ValidationReport>("over there") {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that("location", willFailValidation("over there"), _else(report, "expectedCallback", fieldName()));
            }
        };
        NestedDomainObject outermostNested = new NestedDomainObject<ValidationReport>(innermostNested) {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.associated("nested", report);
            }
        };
        DomainObject domainObject = new DomainObject<ValidationReport>(outermostNested) {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.associated("nested", report);
            }
        };
        ValidationReport report = mock(ValidationReport.class);
        doValidation(domainObject, report);
        verify(report).expectedCallback("nested.nested.location");
    }

    public void testCanFailEarlyWithoutAssociatedObjects() {
        DomainObject domainObject = new DomainObject("name", "email") {
            public void buildValidation(ValidationSyntax validates, Object report) {
                validates.that("name", willFailValidation("name"), _else(report, "expectedCallback"));
                validates.that("email", null, _else(report, "unexpectedCallback"));
            }
        };
        ValidationReport report = mock(ValidationReport.class);
        doFailEarlyValidation(domainObject, report);
        verify(report).expectedCallback();
    }

    private void doFailEarlyValidation(DomainObject domainObject, Object validationReport) {
        DefaultValidationBuilder builder = new DefaultValidationBuilder();
        builder.stopOnFirstFailure();
        domainObject.buildValidation(builder, validationReport);
        builder.validate(domainObject);
    }

    public void testCanFailEarlyWithAssociatedObjects() {
        NestedDomainObject nested = new NestedDomainObject<ValidationReport>("over there") {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that("location", willFailValidation("over there"), _else(report, "expectedCallback"));
                validates.that("location", null, _else(report, "unexpectedCallback"));
            }
        };
        DomainObject domainObject = new DomainObject<ValidationReport>(nested) {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.associated("nested", report);
                validates.that("email", null, _else(report, "unexpectedCallback"));
            }
        };
        ValidationReport report = mock(ValidationReport.class);
        doFailEarlyValidation(domainObject, report);
        verify(report).expectedCallback();
    }

    public void testAssociatedCanDealWillNullReferences() {
        NestedDomainObject nested = new NestedDomainObject<ValidationReport>((NestedDomainObject) null) {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.associated("nested", report);
            }
        };
        DomainObject domainObject = new DomainObject<ValidationReport>(nested) {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.associated("nested", report);
            }
        };
        doValidation(domainObject, mock(ValidationReport.class));
    }

    private ParameterLookupForCallbackMethod parameterLookup(Matcher matcher, String parameterValue) {
        final ParameterLookupForCallbackMethod parameter = mock(ParameterLookupForCallbackMethod.class);
        when(parameter.type(any(DomainObject.class), any(Cardinality.class), same(matcher))).thenReturn(String.class);
        when(parameter.value(any(DomainObject.class), any(Cardinality.class), same(matcher), eq(0))).thenReturn(parameterValue);
        return parameter;
    }

    private void doValidation(DomainObject domainObject, Object report, String... tagsToRemove) {
        DefaultValidationBuilder builder = new DefaultValidationBuilder();
        domainObject.buildValidation(builder, report);
        builder.removeTags(tagsToRemove);
        builder.validate(domainObject);
    }

    private Condition willPassCondition() {
        return stubCondition(true);
    }

    private Condition willFailCondition() {
        return stubCondition(false);
    }

    private Condition stubCondition(final boolean result) {
        return new Condition() {
            public boolean check(Object o) {
                return result;
            }
        };
    }

    private Matcher willFailValidation(Object... value) {
        return matcher(false, value);
    }

    private Matcher willSucceedValidation(Object... value) {
        return matcher(true, value);
    }

    private Matcher matcher(final boolean result, Object... values) {
        Matcher mock = mock(Matcher.class);
        for (Object value : values) {
            when(mock.matches(value)).thenReturn(result);
        }
        return mock;
    }

    public static abstract class DomainObject<R> implements Validatable<R> {
        private NestedDomainObject nested;
        private String name = "mike";
        private String email = "mike@email.com";
        private int age;

        protected DomainObject(String name, String email) {
            this.name = name;
            this.email = email;
        }

        protected DomainObject(int age) {
            this.age = age;
        }

        protected DomainObject(NestedDomainObject nested) {
            this.nested = nested;
        }

        public int aMethodToGetAge() {
            return age;
        }
    }

    public static abstract class NestedDomainObject<R> implements Validatable<R> {
        private String location;
        private String age;
        private NestedDomainObject nested;

        public NestedDomainObject(String location) {
            this.location = location;
        }

        public NestedDomainObject(NestedDomainObject nested) {
            this.nested = nested;
        }
    }

    public static interface ValidationReport {
        void unexpectedCallback();

        void expectedCallback();

        void anotherExpectedCallback();

        void expectedCallback(String parameter);
    }

}
