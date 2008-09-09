package jvalidations.validations;

import jvalidations.Validation;

public abstract class AbstractParameterlessValidation implements Validation {
    public Object parameter(String name) {
        return null;
    }
}
