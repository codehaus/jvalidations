package jvalidations.validations;

import jvalidations.ParameterLookupForCallbackMethod;

public abstract class AbstractSizeValidation extends AbstractParameterizedValidation {
    private final Number limit;

    protected AbstractSizeValidation(Number limit) {
        this.limit = limit;
        registerParameter("limit", limit);
    }

    public boolean check(Object value) {
        return value != null && sizeCheckOk(new Double(value.toString()), limit.doubleValue());
    }

    protected abstract boolean sizeCheckOk(double actual, double limit);


    public static ParameterLookupForCallbackMethod limit() {
        return parameterLookup("limit");
    }


}
