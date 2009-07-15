package jvalidations;

import static jedi.functional.Coercions.list;
import static jvalidations.SyntaxSupport.Parameters.fieldName;
import static jvalidations.SyntaxSupport._else;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Constraint;

public class DefaultValidationBuilderTest extends MockObjectTestCase {

    public void testDoesNothingWhenValidationSucceeds() {
        DomainObject domainObject = new DomainObject<ValidationReport>("mike", "mike@email.com") {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that("name", willSucceedValidation("mike"), _else(report, "unexpectedCallback"));
                validates.that("email", willSucceedValidation("mike@email.com"), _else(report, "unexpectedCallback"));
            }
        };

        doValidation(domainObject, mock(ValidationReport.class));
    }

    public void testCanPassValidationParametersToTheCallbackWhenValidationFails() {
        DomainObject domainObject = new DomainObject<ValidationReport>("mike", null) {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                final Validation validation = willFailValidation("mike");
                validates.that("name", validation,
                        _else(report, "expectedCallback", parameterLookup(validation, "expected value")));
            }
        };

        doValidation(domainObject, validationReport("expectedCallback", eq("expected value")));
    }

    public void testCanMakeACallbackForAllFailedValidations() {
        DomainObject domainObject = new DomainObject<ValidationReport>("mike", "mike@email.com") {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that("name", willFailValidation("mike"), _else(report, "expectedCallback"));
                validates.that("email", willFailValidation("mike@email.com"), _else(report, "anotherExpectedCallback"));
            }
        };

        doValidation(domainObject, validationReport("expectedCallback", "anotherExpectedCallback"));
    }

    public void testCanStateCardinalityForValidationRules() {
        Mock mock = mock(Cardinality.class);
        mock.expects(once()).method("getAccessors").will(returnValue(list(new FieldAccessor("name"), new FieldAccessor("email"))));
        mock.expects(once()).method("requiresMoreChecks").with(eq(0), eq(2)).will(returnValue(true));
        mock.expects(once()).method("requiresMoreChecks").with(eq(0), eq(1)).will(returnValue(true));
        mock.expects(once()).method("happyWith").with(eq(0)).will(returnValue(false));
        final Cardinality cardinality = (Cardinality) mock.proxy();
        DomainObject domainObject = new DomainObject<ValidationReport>("mike", "mike@email.com") {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that(cardinality, willFailValidation("mike", "mike@email.com"), _else(report, "expectedCallback"));
            }
        };
        doValidation(domainObject, validationReport("expectedCallback"));
    }

    public void testCanApplyConditionsToValidationRules() {
        DomainObject domainObject = new DomainObject<ValidationReport>("mike", null) {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that("name", willFailValidation("mike"), _else(report, "expectedCallback")).on(willPassCondition());
            }
        };
        doValidation(domainObject, validationReport("expectedCallback"));

        domainObject = new DomainObject<ValidationReport>("mike", "mike@email.com") {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that("name", willFailValidation(), _else(report, "expectedCallback")).on(willFailCondition());
            }
        };
        doValidation(domainObject, mock(ValidationReport.class));
    }

    public void testCanTagValidationRulesAndExcludeThemDuringValidation() {
        DomainObject domainObject = new DomainObject<ValidationReport>("mike", null) {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that("name", willFailValidation("mike"), _else(report, "expectedCallback")).tags("some tag");
            }
        };
        doValidation(domainObject, validationReport("expectedCallback"));

        domainObject = new DomainObject<ValidationReport>("mike", null) {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that("name", willFailValidation(), _else(report, "expectedCallback")).tags("some tag");
            }
        };
        doValidation(domainObject, mock(ValidationReport.class), "some tag");
    }

    public void testRemovalOfTagsWorksForNestedObjects() {
        NestedDomainObject innermostNested = new NestedDomainObject<ValidationReport>("over there") {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that("location", (Validation) newDummy(Validation.class), _else(report, "expectedCallback", fieldName())).tags("tag");
            }
        };

        NestedDomainObject outermostNested = new NestedDomainObject<ValidationReport>(innermostNested) {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that("location", (Validation) newDummy(Validation.class), _else(report, "expectedCallback", fieldName())).tags("tag");
                validates.associated("nested", report);
            }
        };
        DomainObject domainObject = new DomainObject<ValidationReport>(outermostNested) {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.associated("nested", report);
            }
        };
        doValidation(domainObject, mock(ValidationReport.class), "tag");
    }

    public void testStopOnFirstFailureDoesNotCountFirstTagRemovalAsFailure() {
        DomainObject domainObject = new DomainObject<ValidationReport>("mike", "email") {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that(SyntaxSupport.Cardinalities.atLeast(1).of("name"), (Validation) newDummy(Validation.class), _else(report, "unexpectedCallback")).on(SyntaxSupport.Conditions.condition("name",SyntaxSupport.Validations.isNotNull())).tags("some tag");
                validates.that("email", willFailValidation("email"), _else(report, "expectedCallback", SyntaxSupport.Parameters.fieldName()));
            }
        };
        DefaultValidationBuilder builder = new DefaultValidationBuilder();
        Mock mockReport = mock(ValidationReport.class);
        mockReport.expects(once()).method("expectedCallback").with(eq("email"));
        domainObject.buildValidation(builder, mockReport.proxy());
        builder.removeTags("some tag");
        builder.stopOnFirstFailure();
        builder.validate(domainObject);
    }

    public void testCanMixConditionsAndTags() {
        DomainObject domainObject = new DomainObject<ValidationReport>("mike", null) {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that("name", willFailValidation(), _else(report, "expectedCallback")).on(willFailCondition()).tags("some tag");
            }
        };
        doValidation(domainObject, mock(ValidationReport.class), "some tag");
        doValidation(domainObject, mock(ValidationReport.class));
    }


    public void testAccessorsCanBeMethods() {
        DomainObject domainObject = new DomainObject<ValidationReport>(24) {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.that("aMethodToGetAge()", willFailValidation(24), _else(report, "expectedCallback"));
            }
        };
        doValidation(domainObject, validationReport("expectedCallback"));
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
        doValidation(domainObject, validationReport("expectedCallback", eq("nested.nested.location")));
    }

    public void testCanFailEarlyWithoutAssociatedObjects() {
        DomainObject domainObject = new DomainObject("name", "email") {
            public void buildValidation(ValidationSyntax validates, Object report) {
                validates.that("name", willFailValidation("name"), _else(report, "expectedCallback"));
                validates.that("email", (Validation) newDummy(Validation.class), _else(report, "unexpectedCallback"));
            }
        };
        doFailEarlyValidation(domainObject, validationReport("expectedCallback").proxy());
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
                validates.that("location", (Validation) newDummy(Validation.class), _else(report, "unexpectedCallback"));
            }
        };
        DomainObject domainObject = new DomainObject<ValidationReport>(nested) {
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                validates.associated("nested", report);
                validates.that("email", (Validation) newDummy(Validation.class), _else(report, "unexpectedCallback"));
            }
        };
        doFailEarlyValidation(domainObject, validationReport("expectedCallback").proxy());
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

    private Mock validationReport(String expectedCallbackMethod, Constraint constraints) {
        Mock report = mock(ValidationReport.class);
        report.expects(once()).method(expectedCallbackMethod).with(constraints);
        return report;
    }

    private Mock validationReport(String... expectedCallbackMethods) {
        Mock report = mock(ValidationReport.class);
        for (String expectedCallbackMethod : expectedCallbackMethods) {
            report.expects(once()).method(expectedCallbackMethod);

        }
        return report;
    }

    private ParameterLookupForCallbackMethod parameterLookup(Validation validation, String parameterValue) {
        final Mock parameter = mock(ParameterLookupForCallbackMethod.class);
        parameter.expects(once()).method("type")
                .with(isA(DomainObject.class), isA(Cardinality.class), same(validation))
                .will(returnValue(String.class));
        parameter.expects(once()).method("value")
                .with(isA(DomainObject.class), isA(Cardinality.class), same(validation), eq(0)).will(returnValue(
                parameterValue));
        return (ParameterLookupForCallbackMethod) parameter.proxy();
    }

    private void doValidation(DomainObject domainObject, Mock report, String... tagsToRemove) {
        DefaultValidationBuilder builder = new DefaultValidationBuilder();
        domainObject.buildValidation(builder, report.proxy());
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

    private Validation willFailValidation(Object... value) {
        return validation(false, value);
    }

    private Validation willSucceedValidation(Object... value) {
        return validation(true, value);
    }

    private Validation validation(final boolean result, Object... values) {
        Mock mock = mock(Validation.class);
        for (Object value : values) {
            mock.expects(atLeastOnce()).method("check").with(eq(value)).will(returnValue(result));
        }
        return (Validation) mock.proxy();
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
