package jvalidations;

import junit.framework.TestCase;

import java.util.ArrayList;

public class MethodAccessorTest extends TestCase {

    public void testMeaningfulExceptionIsThrownIfMethodCannotBeFound() {
        MethodAccessor accessor = new MethodAccessor("thisMethodDoesNotExist");
        try {
            accessor.value(new Object());
            fail("Did not get expected exception");
        } catch (RuntimeException e) {
            assertEquals("Could not find method 'thisMethodDoesNotExist' in '" + Object.class + "'", e.getMessage() );
        }
    }
    
    public void testExecutesMethodOnSuppliedInstance() {
        MethodAccessor accessor = new MethodAccessor("getClass");
        Object instance = new Object();
        assertEquals(instance.getClass(), accessor.value(instance));
    }

    public void testExecutesMethodOnSuppliedInstanceEvenIfItsDefinedInTheSuperClass() {
        MethodAccessor accessor = new MethodAccessor("getClass");
        Object instance = new ArrayList();
        assertEquals(instance.getClass(), accessor.value(instance));
    }
}
