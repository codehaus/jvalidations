package jvalidations.functional;

import jedi.functional.Filter;
import jedi.functional.Functor;
import static jedi.functional.Coercions.asList;

import java.util.Collection;

public class Functional {
    public static <I,O> O first(I[] items, Functor<I,O> producer, Filter<O> filter, O defaultResult) {
        return first(asList(items), producer, filter, defaultResult);
    }

    public static <I,O> O first(Collection<I> items, Functor<I,O> producer, Filter<O> filter, O defaultResult) {
        for (I item : items) {
            O product = producer.execute(item);
            if(filter.execute(product)) {
                return product;
            }
        }
        return defaultResult;
    }

    public static <F, T> T find(Functor<F, T> producer,
                           F start,
                           Functor<F, F> next
    ) {
        T result = null;
        F current = start;
        while (result == null && current != null) {
            result = producer.execute(current);
            if (result == null) {
                current = next.execute(current);
            }
        }
        return result;
    }
}
