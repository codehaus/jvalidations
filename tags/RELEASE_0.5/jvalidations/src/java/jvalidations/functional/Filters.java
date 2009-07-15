package jvalidations.functional;

import jedi.filters.AllPassFilter;
import jedi.functional.Filter;

public class Filters {
    public static Filter<Boolean> isFalse() {
        return new Filter<Boolean>() {
            public Boolean execute(Boolean aBoolean) {
                return !aBoolean;
            }
        };
    }

    public static Filter<Boolean> isTrue() {
        return new Filter<Boolean>() {
            public Boolean execute(Boolean aBoolean) {
                return aBoolean;
            }
        };
    }

    public static Filter<Object> notNull() {
        return new Filter<Object>() {
            public Boolean execute(Object o) {
                return o != null;
            }
        };
    }

    public static Filter<Object> all() {
        return new AllPassFilter();
    }

    public static Filter<Object> notBlank() {
        return new Filter<Object>() {
            public Boolean execute(Object o) {
                return o != null && o.toString().trim().length() > 0;
            }
        };
    }
}
