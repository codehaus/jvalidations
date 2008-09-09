package jvalidations.validations;

import jedi.functional.Functor;
import jvalidations.Validation;

import java.util.HashMap;
import java.util.Map;

public class DerivedPropertyValidation implements Validation {
    private final Functor<Object, ? extends Object> producer;
    private final Validation validation;
    private Map<String, String> parameterAliases = new HashMap<String, String>();

    public DerivedPropertyValidation(Functor<Object, ? extends Object> producer,
                              Validation validation) {
        this.producer = producer;
        this.validation = validation;
    }

    public boolean check(Object value) {
        return validation.check(producer.execute(value));
    }

    public Object parameter(String name) {
        if(parameterAliases.containsKey(name)) {
            name = parameterAliases.get(name);
        }
        return validation.parameter(name);
    }

    public DerivedPropertyValidation aliasParameter(String original, String alias) {
        parameterAliases.put(alias, original);
        return this;
    }
}
