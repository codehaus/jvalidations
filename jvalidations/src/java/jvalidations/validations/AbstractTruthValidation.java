package jvalidations.validations;

import java.util.Collection;

public abstract class AbstractTruthValidation extends AbstractParameterlessValidation{
    public boolean check(Object value) {
        return (value != null) && allowedValues().contains(value.toString().toLowerCase());
    }

    protected abstract Collection<String> allowedValues();

}
