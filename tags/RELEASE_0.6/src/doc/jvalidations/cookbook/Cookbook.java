package jvalidations.cookbook;

import static jvalidations.SyntaxSupport._else;
import static jvalidations.SyntaxSupport.Parameters.*;
import static jvalidations.SyntaxSupport.Conditions.condition;
import static jvalidations.SyntaxSupport.Cardinalities.atLeast;
import jvalidations.Validatable;
import jvalidations.ValidationSyntax;
import jvalidations.DefaultValidationBuilder;
import jvalidations.SyntaxSupport;
import org.hamcrest.core.*;
import static org.hamcrest.core.DescribedAs.describedAs;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import org.hamcrest.text.IsEmptyString;
import static org.hamcrest.text.IsEmptyString.isEmptyString;
import org.hamcrest.number.OrderingComparison;
import static org.hamcrest.number.OrderingComparison.greaterThan;

public class Cookbook {
    /**
     * This page explains how to do the most common validation tasks and should be read following the tutorial, or for reference.
     */
    public static class Section_ValidatingValuesReturnedFromMethods {
        /**
         * Most places where you use a field name you can also use a method name.  The method must take no arguments
         * and you indicate its a method simply by putting <b>()</b> at the end of it.
         */
        public static class Customer implements Validatable<Customer.ValidationReport> {

            public interface ValidationReport {
                void isNull(String fieldOrMethodName);
            }

            public String someQueryMethod() {
                return null;
            }

            public void buildValidation(ValidationSyntax validates,
                                        ValidationReport report) {
                validates.that("someQueryMethod()", notNullValue(), _else(report, "isNull", fieldName()));
            }
        }

        /**
         * This will call <domaincode>someQueryMethod()</domaincode> on the instance of <domaincode>Customer</domaincode> being
         * validated, then validate that the result of that method is not null.
         */
    }//ignore

    public static class Section_SpecifyingParametersForYourValidationFailureCallbackMethod {
        /**
         * There may be a time where you want to have parameters from the context of the validation rule passed
         * through to your validation failure callback method.  We have already seen this some for <method>fieldName()</method>.
         * But other possibilities exist like:
         * <br/>
         * <ul>
         * <li><method>requiredCount()</method> - the number specified in a cardinality.  For example, it would be 2 in <method>atLeast(2)</method></li>
         * <li><method>actualCount()</method> - the number of successful matches in a rule with cardinality. For example, if you stated <method>atLeast(2)</method> of something, but only 1 was successful, this number would be 1</li>
         * <li><method>fieldNames()</method> - the names of the fields in a rule with cardinality. For example, if you stated <method>atLeast(1).of("age","name")</method>  this would be a string array containing "age" and "name"</li>
         * <li><method>fieldName()</method> - the name of the field in a rule without cardinality. For example, if you stated <method>validates.that("age",...</method> this would be the string "age"</li>
         * <li><method>constant()</method> - any value that you specify as a constant will be passed through untouched</li>
         * <li><method>failureDescription()</method> - a string describing the validation failure.  This string is built into the particular Hamcrest matcher.  You can specify your own string using <method>DescribedAs.describedAs()</method> as shown below.</li>
         * </ul>
         * <br/>
         * Some examples:
         */
        public static class Customer implements Validatable<Customer.ValidationReport> {
            private String name;
            private int age;

            public interface ValidationReport {
                void describedFailure(String fieldName, String failureDescription);
                void failureWithErrorCode(String errorCode);
            }

            public void buildValidation(ValidationSyntax validates,
                                        ValidationReport report) {
                validates.that("name", notNullValue(), _else(report, "describedFailure", fieldName(), failureDescription()));
                validates.that("age", 
                        describedAs("ErrorCode 1002",greaterThan(21)),
                        _else(report, "failureWithErrorCode", failureDescription()));
            }
        }
        /**
         * All of these parameter methods are available as static imports from <class>SyntaxSupport.Parameters</class>.
         */
    }//ignore

    public static class Section_ValidatingMultipleFields {
        /**
         * Its very common to want to say "at least 2 of these 3 fields should be non null":
         */
        public static class Customer implements Validatable<Customer.ValidationReport>{
            private String name;
            private String email;
            private String level;

            public interface ValidationReport {
                void isNull(String []fieldNames, int requiredCount, int ActualCount);
            }

            public void buildValidation(ValidationSyntax validates,
                                        ValidationReport report) {
                validates.that(
                        atLeast(2).of("name","email","level"),
                        notNullValue(),
                        _else(report, "isNull", fieldNames(), requiredCount(), actualCount()));
            }
        }

        /**
         * <method>atLeast()</method> is available as a static method on <class>SyntaxSupport.Cardinalities</class>, along with other cardinality methods like
         * <method>exactly()</method>, <method>all()</method> and <method>both</method>.
         * <br/>
         * <br/>
         * <method>fieldNames()</method> is available as a static method on <class>SyntaxSupport.Parameters</class>.  It will make the list of field names in the validation
         * available as a String array to your callback method.  <method>requiredCount()</method> and <method>actualCount()</method> are also on <class>SyntaxSupport.Parameters</class>
         * and make the number of fields required by the validation cardinality and the actual number of valid fields respectively available to you callback method.
         */
    }//ignore

    public static class Section_ValidatingAssociatedObjects {
        /**
         * Lets say our <domaincode>Customer</domaincode> has an <domaincode>Address</domaincode> object associated with it and when validating the <domaincode>Customer</domaincode>
         * we want to rope in validation of the <domaincode>Address</domaincode>.  Well lets walk through that.  The first step is to make the <domaincode>Address</domaincode>
         * validatable...
         */
        public static class Address implements Validatable<Address.ValidationReport>{
            private String addressLine1;
            private String addressLine2;
            private String city;
            private String postCode;

            public interface ValidationReport {
                void required(String fieldName);
            }

            public void buildValidation(ValidationSyntax validates,
                                        ValidationReport report) {
                validates.that("addressLine1", not(isEmptyString()), _else(report, "required", fieldName()));
                validates.that("postCode", not(isEmptyString()), _else(report, "required", fieldName()));
            }
        }

        /**
         * And now lets look at what we have to do with our <domaincode>Customer</domaincode>.  The main thing to look out for is that the validation report
         * in <domaincode>Customer</domaincode> must extend the validation report in <domaincode>Address</domaincode>.
         * Then all you do is <method>validates.associated()</method>
         */
        public static class Customer implements Validatable<Customer.ValidationReport>{
            private String name;
            private String email;
            private Address address;

            public interface ValidationReport extends Address.ValidationReport {
                void isBlank(String fieldName);
            }

            public void buildValidation(ValidationSyntax validates,
                                        ValidationReport report) {
                validates.that("name", not(isEmptyString()), _else(report, "isBlank", fieldName()));
                validates.that("email", not(isEmptyString()), _else(report, "isBlank", fieldName()));
                validates.associated("address", report);
            }
        }
        /**
         * The first two validation rules are for the fields on the <domaincode>Customer</domaincode>, and the third line says
         * "descend into the address instance and validate it too".  There may be futher <method>validates.associated()</method>
         * calls in <domaincode>Address</domaincode> if needed.  Just remember that the validation report of a domain class
         * needs to extend all the validation reports of its children.
          */
    }//ignore

    public static class Section_InheritValidationRulesFromSuperClass {
        /**
         * If you have got validation rules in a super class, you will also want to make sure they apply to sub classes.
         * If you want to do this there is a few bits of garbage you need to do with the generics:
         * <ul>
         * <li>Your super class needs to be genericized on the validation report, guaranteeing that it is at least a sub class
         * of its own validation report</li>
         * <li>Your sub class needs to bind its validation report to the abstract class and it does not need to implement
         * <interface>Validatable</interface>.</li>
         * </ul>
         * Goodness - I find this stuff so hard to remember.
         * <br/>
         * <br/>
         * Now you can call <method>super.buildValidation()</method> and ensure that the sub class validation report
         * extends the super class validation report
         */
        public static abstract class AbstractCustomer<R extends AbstractCustomer.ValidationReport> implements Validatable<R>{
            private String name;

            public interface ValidationReport {
                void isBlank(String fieldName);
            }

            public void buildValidation(ValidationSyntax validates,
                                        R report) {
                validates.that("name",not(isEmptyString()), _else(report, "isBlank", fieldName()));
            }
        }
        /**
         * And now the subclass...
         */
        public static class Customer extends AbstractCustomer<Customer.ValidationReport>{
            private String email;

            public interface ValidationReport extends AbstractCustomer.ValidationReport {
                void required(String fieldName);
            }

            public void buildValidation(ValidationSyntax validates,
                                        ValidationReport report) {
                super.buildValidation(validates, report);
                validates.that("name",not(isEmptyString()), _else(report, "isBlank", fieldName()));
            }
        }
        /**
         * Notice that <method>buildValidation()</method> in the super class uses <class>R</class> as its type.  And that the sub class
         * does not need to implement <interface>Validatable</interface> because it binds its version of the validation report to the
         * <method>buildValidation()</method> in the super class.  Phew!
         */
    }//ignore

    public static class Section_MakingValidationRulesConditional {
        /**
         * Sometimes you only want certain validation rules to fire in certain cases.  For example, only validate address line 2
         * if address line 1 has a value.  Or only validate the address of a customer if the customer is active.  You can do this kind
         * of thing by tacking an <method>on()</method> clause onto the end of your validation rule.
         */
        public static class Address implements Validatable<Address.ValidationReport>{
            private String addressLine1;
            private String addressLine2;

            public interface ValidationReport {
                void required(String fieldName);
            }

            public void buildValidation(ValidationSyntax validates,
                                        ValidationReport report) {
                validates.that("addressLine1", not(isEmptyString()), _else(report, "required", fieldName()));
                validates.that(
                        "addressLine2",
                        not(isEmptyString()),
                        _else(report, "required", fieldName())
                ).on(condition("addressLine1", not(isEmptyString())));
            }
        }
        /**
         * <method>condition()</method> is available as a static method on <class>SyntaxSupport.Conditions</class>.  In this case we are supplying it a field name
         * and a validation.  Only if this is true for the object being validated will the associated validation rule fire.  A few things to note:
         * <ul>
         * <li>If you omit the validation part, <method>TrueValidation.isTrue()</method> will be used instead.  So you can say <method>on(condition("isActive"))</method></li>
         * <li>Zero arg methods can be used in place of field names.  So you can say <method>on(condition("isActive()"))</method></li>
         * </ul>
         * <br/>
         * <br/>
         * You can use the logical operators for validation rules like and and or and not in these conditions - see the section below on logical operators. 
         */
    }//ignore

    public static class Section_LogicalOperationsForValidationRules {
        /**
         * Sometimes you may want to combine validations into a logical expression to state your intent.
         * I am talking about the hamcrest <method>AllOf.allOf()</method>, <method>AnyOf.anyOf()()</method>, <method>IsNot.not()</method> and the like.
         * <br/>
         * <br/>
         * Say you want a string to either start with Jo or Ac....
         */
        public static class Customer implements Validatable<Customer.ValidationReport>{
            private String name;

            public interface ValidationReport {
                void somethingWrong();
            }

            public void buildValidation(ValidationSyntax validates,
                                        ValidationReport report) {
                validates.that(
                        "name",
                        AnyOf.anyOf(StringStartsWith.startsWith("Jo"),StringStartsWith.startsWith("Ac")),
                        _else(report, "somethingWrong")); //same as isBetween()
            }
        }
        /**
         * The same kind of thing goes for <method>AllOf.allOf()()</method> and <method>IsNot.not()</method>.
         */
    }//ignore

    public static class Section_SelectingWhichValidationRulesToFireAtValidationTime {
        /**
         * Sometimes you want to fire a particular arrangement of validation rules based on conditions that are
         * only known to the caller.  The <method>on()</method> clause lets you state conditions for validation rules
         * that depend on the objects state.  But what if you want to exclude a particular validation rule because, say,
         * the class doing the validation knows that a special offer is on today and a certain rule therefore does not apply.
         * <br/>
         * <br/>
         * Well you can do this by tagging your validation rules, then excluding particular tags at validation time. A tag can be any old object by the way.
         */
        public static class Customer implements Validatable<Customer.ValidationReport>{
            private int creditAvailable;

            public interface ValidationReport {
                void insufficientCredit();
            }

            public void buildValidation(ValidationSyntax validates,
                                        ValidationReport report) {
                validates.that(
                        "creditAvailable",
                        greaterThan(1000),
                        _else(report, "insufficientCredit")
                ).tags("Due Diligence");
            }
        }

        public void demonstrateRemovingTags() {
            Customer customer = new Customer();
            Customer.ValidationReport report = null; //its only a demo
            DefaultValidationBuilder builder = new DefaultValidationBuilder();
            customer.buildValidation(builder, report);
            builder.removeTags("Due Diligence");
            builder.validate(customer);
        }
        /**
         * And now, minus your due diligence,  you can have your very own credit crunch :-)
         */
    }//ignore

    public static class Section_ExternalizingTheDefinitionOfValidationRules {
        /**
         * This is something that came up recently only in discussion and has not been thought through, spiked
         * or implemented :-)  But its not uncommon for a need to arise to externalize validation rules for a business
         * object.  While one of the goals of JValidations is to encapsulate the validation rules for an object, it is
         * worthwhile considering if validation rules for an object could be externalised through the use of OGNL say.
         * It would be possible to use the JValidation syntax in that OGNL file, and have the domain object load its
         * definition from the OGNL file and that might solve the problem.  But this is an exercise for another day, and
         * its just noted here for interest.
         */

    }//ignore

    public static class Section_PuttingThingsTogetherToDefineYourOwnValidationCallbackNamingScheme {
    }//ignore
    /*END*/
}
