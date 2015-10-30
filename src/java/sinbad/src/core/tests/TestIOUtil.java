package core.tests;

import static core.sig.PrimSig.BOOLEAN_SIG;
import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import core.ops.SigClassUnifier;
import core.ops.SignatureUnificationException;
import core.util.IOUtil;

public class TestIOUtil {
    @Rule
    public final ExpectedException ex = ExpectedException.none();

    @Test
    public void testCreateInput() {
        assertNotNull(IOUtil.createInput("http://www.google.com"));
        assertNotNull(IOUtil.createInput("src/core/tests/TestIOUtil.java"));
        assertNotNull(IOUtil.createInput("http://archive.ics.uci.edu/ml/machine-learning-databases/00280/HIGGS.csv.gz"));
    }
    
    @Test
    public void testCreateInputNull() {
        assertNull(IOUtil.createInput("http://cs.berry.edu/noway"));
        assertNull(IOUtil.createInput("src/core/tests/NOTINHERE.BAD"));
    }
    
    @Test
    public void testCreateInputFail1() {
        ex.expect(RuntimeException.class);
        IOUtil.createInput("src/core/tests/testioutil.java");
    }


}
