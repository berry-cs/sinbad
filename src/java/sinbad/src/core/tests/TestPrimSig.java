package core.tests;

import org.junit.*;
import core.sig.*;

import static org.junit.Assert.*;
import static core.sig.PrimSig.*;


public class TestPrimSig {

    static ISigVisitor<Integer> intVisitor = new ISigVisitor<Integer>() {
        public Integer defaultVisit(ISig s) { return 0; }
        public <C> Integer visit(PrimSig s) { return 1; }
        public Integer visit(CompSig<?> s) { return 2; }
        public Integer visit(ListSig s) { return 3; } 
    };

    
    @Test
    public void testApply() {
        assertEquals(BOOLEAN_SIG.apply(intVisitor), 1);
    }
    
    @Test
    public void testToString() {
        assertEquals(BOOLEAN_SIG.toString(), "<boolean>");
        assertEquals(INT_SIG.toString(), "<int>");
        assertEquals(STRING_SIG.toString(), "<String>");
    }
}
