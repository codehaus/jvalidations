package jvalidations;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MethodAccessorTest  {

    @Test
    public void testMeaningfulExceptionIsThrownIfMethodCannotBeFound() {
        MethodAccessor accessor = new MethodAccessor("thisMethodDoesNotExist()");
        try {
            accessor.value(new Object());
            fail("Did not get expected exception");
        } catch (RuntimeException e) {
            assertEquals("Could not find method 'thisMethodDoesNotExist' in '" + Object.class + "'", e.getMessage() );
        }
    }
    
    @Test
    public void testExecutesMethodOnSuppliedInstance() {
        MethodAccessor accessor = new MethodAccessor("getClass()");
        Object instance = new Object();
        assertEquals(instance.getClass(), accessor.value(instance));
    }

    @Test
    public void testExecutesMethodOnSuppliedInstanceEvenIfItsDefinedInTheSuperClass() {
        MethodAccessor accessor = new MethodAccessor("getClass()");
        Object instance = new ArrayList();
        assertEquals(instance.getClass(), accessor.value(instance));
    }

    @Test
    public void testExecutesNestedMethod() {
        MethodAccessor accessor = new MethodAccessor("stringOf(myValue())");
        Object instance = new TestObject(43);
        assertEquals("43", accessor.value(instance));

    }

    public static class TestObject {
        private final int value;

        public TestObject(int value) {
            this.value = value;
        }

        public String stringOf(Integer in) {
            return in.toString();
        }

        public Integer myValue() {
            return value;
        }
    }
}
