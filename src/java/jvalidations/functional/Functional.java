package jvalidations.functional;

import jedi.functional.Filter;
import jedi.functional.Functor;
import jedi.functional.FirstOrderLogic;
import jedi.functional.FunctionalPrimitives;
import static jedi.functional.FunctionalPrimitives.collect;
import static jedi.functional.FirstOrderLogic.not;
import static jedi.functional.Coercions.asList;

import java.util.Collection;

public class Functional {
    public static <I,O> O first(I[] items, Functor<I,O> producer, Filter<O> filter, O defaultResult) {
        return first(asList(items), producer, filter, defaultResult);
    }

    public static <I> boolean all(Collection<I> items, Functor<I,Boolean> producer, Filter<Boolean> truthCheck) {
        return FirstOrderLogic.all(collect(items,producer),truthCheck);

    }
    public static <I,O> O first(Collection<I> items, Functor<I,O> producer, Filter<O> truthCheck, O defaultResult) {
        for (I item : items) {
            O product = producer.execute(item);
            if(truthCheck.execute(product)) {
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
