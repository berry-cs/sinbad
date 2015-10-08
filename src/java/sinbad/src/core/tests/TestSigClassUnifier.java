package core.tests;

import org.junit.*;
import org.junit.rules.ExpectedException;

import core.sig.*;
import core.ops.*;

import static org.junit.Assert.*;
import static core.sig.PrimSig.*;

public class TestSigClassUnifier {
    @Rule
    public final ExpectedException ex = ExpectedException.none();

    public TestSigClassUnifier() {
        
    }

    @Test
    public void testPrimSigSucceed() {
        assertEquals(BOOLEAN_SIG, BOOLEAN_SIG.apply(new SigClassUnifier(Boolean.class)));
        assertEquals(STRING_SIG, BOOLEAN_SIG.apply(new SigClassUnifier(String.class, true))); // widening conversion
    }
    
    @Test
    public void testPrimSigFail1() {
        ex.expect(SignatureUnificationException.class);
        BOOLEAN_SIG.apply(new SigClassUnifier(Float.class));
    }
    
    @Test
    public void testPrimSigFail2() {
        ex.expect(SignatureUnificationException.class);
        BOOLEAN_SIG.apply(new SigClassUnifier(String.class));  // no widening
    }
    
    
    
    @Test
    public void testListSigSucceed() {
        int[] nums = {};
        ListSig s1 = new ListSig(INT_SIG);
        assertEquals(s1, s1.apply(new SigClassUnifier(nums.getClass())));

        String[][] wds = {};
        ListSig s2 = new ListSig(new ListSig(STRING_SIG));
        assertEquals(s2, s2.apply(new SigClassUnifier(wds.getClass())));
    }
    
    @Test
    public void testListSigFail1() {                // listof int   with  String[]
        ex.expect(SignatureUnificationException.class);
        String[] nums = {};
        new ListSig(INT_SIG).apply(new SigClassUnifier(nums.getClass(), true));
    }
    
    @Test
    public void testListSigFail2() {                // listof-listof-string  with  int[][]
        ex.expect(SignatureUnificationException.class);
        int[][] wds = {};
        ListSig s2 = new ListSig(new ListSig(STRING_SIG));
        s2.apply(new SigClassUnifier(wds.getClass()));  
    }
}
