package jvalidations.validations;

import static jedi.functional.Coercions.list;
import jvalidations.AbstractJValidationsTestCase;

import java.io.Serializable;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import java.util.List;

public class TrueValidationTest extends AbstractJValidationsTestCase {
    static List<? extends Serializable> TRUE_LIST = list("y","Y","yes","YES","true","TRUE",true, TRUE);
    static List<? extends Serializable> FALSE_LIST = list("n","N","no","NO","false","FALSE",false, FALSE);
    static List<Object> NEITHER_FALSE_OR_TRUE_LIST = list(null, "", new Object());


    public void testIsTrueReturnsTrueWhenSuppliedObjectIsTrueOrAStringRepresentingTrue() {
        assertAllTrue(check(TrueValidation.isTrue(), TRUE_LIST));
        assertAllFalse(check(TrueValidation.isTrue(), FALSE_LIST));
        assertAllFalse(check(TrueValidation.isTrue(), NEITHER_FALSE_OR_TRUE_LIST));
    }

    public void testIsNotTrueReturnsTrueWhenSuppliedObjectIsNotTrueOrAStringRepresentingTrue() {
        assertAllFalse(check(TrueValidation.isNotTrue(), TRUE_LIST));
        assertAllTrue(check(TrueValidation.isNotTrue(), FALSE_LIST));
        assertAllTrue(check(TrueValidation.isNotTrue(), NEITHER_FALSE_OR_TRUE_LIST));
    }

    public void testHasNoParameters() {
        assertAllNull(parameters(TrueValidation.isTrue(), edgeCaseParameterNames()));
        assertAllNull(parameters(TrueValidation.isNotTrue(), edgeCaseParameterNames()));
    }

}
