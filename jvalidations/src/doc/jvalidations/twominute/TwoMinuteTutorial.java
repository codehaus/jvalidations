package jvalidations.twominute;

import static jvalidations.SyntaxSupport._else;
import static jvalidations.SyntaxSupport.Parameters.fieldName;
import jvalidations.*;
import static jvalidations.validations.BlankValidation.isNotBlank;

public class TwoMinuteTutorial {
    /**
     * Lets say you have this domain object, a <domaincode>Customer</domaincode> with a name and an email address:
     */
    public static class StepOne {//ignore

        public class Customer {
            private final String name;
            private final String email;

            public Customer(String name, String email) {
                this.name = name;
                this.email = email;
            }
        }
    }//ignore

    /**
     * And lets say you want to validate that <domaincode>name</domaincode> is not blank.  The first step, which might not seem
     * directly relevant now but keep reading and you'll soon see why,
     * is to create an interface that defines a method that will be called in the event of validation failing.
     * As a convention we call this the <domaincode>ValidationReport</domaincode> and we make it an interface nested in the domain object.
     * In our case we will have a single method that will be called in the case of name being blank i.e. validation failing:
     */
    public static class StepTwo {//ignore

        public static class Customer {
            private final String name;
            private final String email;

            public Customer(String name, String email) {
                this.name = name;
                this.email = email;
            }

            public interface ValidationReport {
                void nameIsBlank();
            }
        }
    }//ignore

    /**
     * Declaring an interface like this is an abstract statement by your <domaincode>Customer</domaincode> of what it requires
     * of classes attempting to validate it.  It is saying, "if you want to validate me, you need to be able to handle callbacks
     * where I tell you <domaincode>nameIsBlank()</domaincode>".  The class doing the validation therefore needs to supply an
     * implementation of this interface, and can do as it pleases when its told that <domaincode>nameIsBlank()</domaincode>.
     * This is the <a href="http://c2.com/cgi/wiki?DependencyInversionPrinciple">dependency inversion principle</a> and it makes for good designs :-).
     * <br/>
     * <br/>
     * The next step is to make <domaincode>Customer</domaincode> validatable.  We do this by making it implement <interface>Validatable</interface>,
     * which will require you to implement <method>buildValidation</method>.
     * This interface is genericized on the validation report.  Code makes it clearer:
     */
    public static class StepThree {//ignore

        public static class Customer implements Validatable<Customer.ValidationReport> {
            private final String name;
            private final String email;

            public Customer(String name, String email) {
                this.name = name;
                this.email = email;
            }

            public interface ValidationReport {
                void nameIsBlank();
            }

            public void buildValidation(ValidationSyntax validates, ValidationReport report) {
                //Your validation rules will go here
            }
        }
    }//ignore

    /**
     * And now all we have to do is define our validation rule.  As you can see from the snippet above, we put our validation rules in the
     * <method>buildValidation</method> method.  The first parameter is an instance of the JValidations
     * interface <interface>ValidationSyntax</interface>.  This interface provides an api for expressing your validation rules.
     * Name the parameter <param>validates</param>
     * if you can, because it helps with readability, as you will see later.
     * <br/>
     * <br/>
     * The second parameter is an instance of your validation report interface.  Name it <param>report</param> if you can, because
     * again, it helps with readability.
     * <br/>
     * <br/>
     * The general form of a validation statement is
     * <br/>
     * <br/>
     * <code>validates.that(field_name, validation_rule, else_clause)</code>
     * <br/>
     * <br/>
     * <b>field_name</b> is a string naming the field of this class that must be validated.
     * <br/>
     * <br/>
     * <b>validation_rule</b> is an instance of the JValidations interface <interface>Validation</interface>.  You will find many
     * implementations of this interface available as static methods on classes in the package <package>jvalidations.validations</package> -
     * <class>BlankValidation</class> for example has the static method <method>isNotBlank()</method>.
     * <br/>
     * <br/>
     * <b>else_clause</b> is an instance of the JValidations interface <interface>ElseClause</interface>.  The <b>else_clause</b> states
     * what to do if validation fails.  Generally the idea is to call a method on your validation report, so this clause will state what method
     * to call on your validation report, and with what parameters. There is a static method
     * <method>_else</method> on the JValidations class <class>SyntaxSupport</class> to help you build these else clauses, and we will go into it
     * in more detail later, but for now lets see some code:
     */
    public static class StepFour {//ignore
        public interface ValidationReport{}//ignore
        public void buildValidation(ValidationSyntax validates, ValidationReport report) {
            validates.that("name", isNotBlank(), _else(report,"nameIsBlank"));
        }
    }//ignore

    /**
     * <b>Note</b> that <method>isNotBlank()</method> and <method>_else()</method> have been statically imported for readability.
     * <br/>
     * <br/>
     * So you can see that this <b>else_clause</b> is saying "call the method <method>nameIsBlank()</method> on the <b>report</b> instance
     * with no parameters".  We will cover cases where we want to pass parameters to the <b>report</b> methods later on.  For now though, lets see
     * what happens if we also want to validate that email is not blank.
     * <br/>
     * <br/>
     * Our first decision is what validation report method to call in the event of validation failure.  We could add a method <domaincode>emailIsBlank</domaincode>,
     * but this is not a scalable solution as we increase the number of fields and the number of validation rules.  Better to define a single method for
     * failed "not blank" validations, with the offending field named as a parameter.  Alright then, lets first rework the validation report interface:
     */
    public static class StepFive {//ignore
        public interface ValidationReport {
            void isBlank(String fieldName);
        }
    }//ignore

    /**
     * And now lets rework our existing "not blank" validation rule for <domaincode>name</domaincode>.  I said earlier that <method>SyntaxSupport._else()</method>
     * supports parameters to the validation report methods.  It can take any number of <interface>ParameterLookupForCallbackMethod</interface> instances, one for
     * each parameter that the validation report method requires.  Again, there are static methods on <class>SyntaxSupport.Parameters</class> that create these instances
     * for you, and one of them is <method>fieldName()</method>.  So now we can rework our existing validation rule:
     */
    public static class StepSix {//ignore
        public interface ValidationReport{}//ignore
        public void buildValidation(ValidationSyntax validates, ValidationReport report) {
            validates.that("name", isNotBlank(), _else(report,"isBlank", fieldName()));
        }
    }//ignore

    /**
     * And adding our <domaincode>email</domaincode> validation is easy:
     */
    public static class StepSeven {//ignore
        public interface ValidationReport{}//ignore
        public void buildValidation(ValidationSyntax validates, ValidationReport report) {
            validates.that("name", isNotBlank(), _else(report,"isBlank", fieldName()));
            validates.that("email", isNotBlank(), _else(report,"isBlank", fieldName()));
        }
    }//ignore

    /**
     * So thats how to make a <domaincode>Customer</domaincode> validatable.  Now you probably want to know how to actually
     * check if a given <domaincode>Customer</domaincode> instance is valid.  Our first step therefore is to make an
     * implementation of <domaincode>Customer.ValidationReport</domaincode> that does something when told there is something
     * invalid with the <domaincode>Customer</domaincode>.
     */
    public static class StepEight {//ignore
        public static class Customer implements Validatable<Customer.ValidationReport>{//ignore
            private final String name;//ignore
            private final String email;//ignore
            public Customer(String name, String email) {//ignore
                this.name = name;//ignore
                this.email = email;//ignore
            }//ignore
            public void buildValidation(ValidationSyntax validates, ValidationReport report) {//ignore
                validates.that("name", isNotBlank(), _else(report,"isBlank", fieldName()));//ignore
                validates.that("email", isNotBlank(), _else(report,"isBlank", fieldName()));//ignore
            }//ignore
            public interface ValidationReport {//ignore
                void isBlank(String fieldName);//ignore
            }//ignore
        }//ignore
        public static class PrintingValidationReport implements Customer.ValidationReport {
            public void isBlank(String fieldName){
                System.out.println(fieldName + " is blank");
            }
        }

        /**
         * And now we have all the bits and pieces to make the validation check.  Instantiate your <domaincode>Customer</domaincode>
         * and validation report instances, and then pass them to <method>JValidations.validate()</method>.
         */
        public void demonstrateValidation() {
            Customer customer = new Customer("Ernie","");
            Customer.ValidationReport report = new PrintingValidationReport();
            JValidations.validate(customer, report);
        }

        /**
         * And if you call this method you will see "email is blank" printed to stdout.
         */
    }//ignore
    /*END*/

    public static void main(String ...args) {
        StepEight.Customer customer = new StepEight.Customer("Ernie","");
        StepEight.Customer.ValidationReport report = new StepEight.PrintingValidationReport();
        JValidations.validate(customer, report);
    }
}
