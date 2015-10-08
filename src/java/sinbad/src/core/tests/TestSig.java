package core.tests;

import org.junit.*;
import core.sig.*;

import static org.junit.Assert.*;
import static core.sig.PrimSig.*;

/**
 * Unit tests for core.sig classes
 *
 */
public class TestSig {

    static ISigVisitor<Integer> intVisitor = new ISigVisitor<Integer>() {
        public Integer defaultVisit(ISig s) { return 0; }
        public Integer visit(PrimSig s) { return 1; }
        public Integer visit(CompSig<?> s) { return 2; }
        public Integer visit(ListSig s) { return 3; } 
    };

    
    @Test
    public void testApply() {
        assertEquals(1, BOOLEAN_SIG.apply(intVisitor));
    }
    
    @Test
    public void testToString() {
        assertEquals("<boolean>", BOOLEAN_SIG.toString());
        assertEquals("<int>", INT_SIG.toString());
        assertEquals("<String>", STRING_SIG.toString());
    }
}
