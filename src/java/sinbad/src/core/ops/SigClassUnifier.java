package core.ops;

import java.util.ArrayList;
import org.apache.commons.lang3.*;
import core.sig.*;

import static core.log.Errors.*;
import static core.sig.PrimSig.*;


/**
 * Checks that the type represented by a signature is consistent
 * with the actual Java type represented by a given Class. This
 * operation produces a specialization of the initial signature, 
 * if appropriate.
 * 
 * This operation throws a SignatureUnificationException if it
 * fails.
 */
public class SigClassUnifier implements ISigVisitor<ISig> {
    private Class<?> c;  // the class against which the signature is being unified
    private boolean allowWidening;
    
    public SigClassUnifier(Class<?> c, boolean allowWidening) {
        this.c = c;
        this.allowWidening = allowWidening;
    }
    
    public SigClassUnifier(Class<?> c) {
        this(c, false);
    }

    @Override
    public ISig defaultVisit(ISig s) {
        throw exception(SignatureUnificationException.class, "scunify:unknown-sig"); 
    }

    @Override
    public ISig visit(PrimSig s) {
        if (ArrayUtils.indexOf(new Object[] { Boolean.class, Byte.class, 
                                              Short.class, Long.class,
                                              Character.class, Float.class, 
                                              Integer.class, Double.class, 
                                              String.class }, c) == ArrayUtils.INDEX_NOT_FOUND
                    && !c.isPrimitive()) {
            throw exception(SignatureUnificationException.class, "scunify:not-prim", c.getName());
        }

        PrimSig csig = PrimSig.primSigFor(c);   // sig for Class c 
        if (this.allowWidening && c == String.class) return STRING_SIG;
        else if (s == WILDCARD_SIG) return csig;  // safe to use == here because PrimSig's are singleton objects
        else if (s == csig) return csig;
        else throw exception(SignatureUnificationException.class, "scunify:prim-mismatch", 
                             s.toString(), c.getName());

    }

    @Override
    public ISig visit(CompSig<?> s) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ISig visit(ListSig s) {
        if (c.isArray()) {
            Class<?> ce = c.getComponentType();
            try {
                return new ListSig(s.getElemType().apply(new SigClassUnifier(ce, false)));
            } catch (SignatureUnificationException e) {
                throw exception(SignatureUnificationException.class, "scunify:list-fail/bc", s, c.getSimpleName(), e.getMessage());
            }
        } else if (c == ArrayList.class) {
            // not possible to reflectively get element type of arraylist
            return s;
        } else {
            throw exception(SignatureUnificationException.class, "scunify:list-fail", s, c.getSimpleName());
        }
    }

}
