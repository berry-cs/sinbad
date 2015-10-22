package core.ops;

import java.lang.reflect.*;
import java.util.ArrayList;
import org.apache.commons.lang3.*;

import core.sig.*;
import core.util.ProcessingDetector;
import static core.log.Errors.*;
import static core.sig.PrimSig.*;


/**
 * Checks that the type represented by a signature is consistent
 * with the actual Java type represented by a given Class. This
 * operation produces a specialization of the initial signature, 
 * if appropriate.
 * 
 * <p>The primary utility of this class is the findConstructor
 * method, which finds a matching constructor of a class
 * associated with a compound signature object. This constructor
 * is then used to instantiate objects using data from a DataAccess
 * source with a schema that has been unified against the 
 * signature. 
 * 
 * TODO: document allowWidening
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

    /**
     * Finds the appropriate constructor of class C whose Java type
     * signature can be unified against that of the given 
     * CompSig signature object.
     * 
     * @param s a compound signature 
     * @return a pair of the constructor found, as well as a specialization 
     * of <code>s</code> as appropriate 
     */
    public static <C> ConstructorSigPair<C> findConstructor(CompSig<C> s) throws SignatureUnificationException {

        Class<C> c = s.getAssociatedClass();
        Constructor<C>[] constrs = (Constructor<C>[]) c.getDeclaredConstructors();  // get all constructors of class C

        for (Constructor<C> cr : constrs) {
            int m = cr.getModifiers();
            if (Modifier.isPrivate(m) || Modifier.isProtected(m)) // ignore private/protected constructors
                continue;
            
            CompSig<C> sC = unifyWithConstructor(s, cr, false);  // try this first
            if (sC == null) {                           // if it doesn't work...
                sC = unifyWithConstructor(s, cr, true); // try to account for Processing weirdness
            }
            if (sC != null) { // one or the other worked
                return new ConstructorSigPair<C>(cr, sC);
            }
        }
        throw exception(SignatureUnificationException.class, "scunify:unify-fail", s, c.getSimpleName());
    }
    
    
    /**
     * Attempt to unify the given compound signature with a specific constructor for class C.
     * Return a specialized signature on success, or null if it cannot unify.
     * 
     * @param processingCompatible flag indicating whether to account for weird things 
     *                             Processing might do with constructors
     * @return a specialized signature or null
     */
    private static <C> CompSig<C> unifyWithConstructor(CompSig<C> s, Constructor<C> cr, boolean processingCompatible) {
        Class<?>[] paramTys = cr.getParameterTypes();
        int start = 0;
        if (processingCompatible && ProcessingDetector.inProcessing()
                // processing adds the PApplet as parameter to 
                // classes that are part of a user's sketch
                && paramTys.length == 1 + s.getFieldCount()  
                && ProcessingDetector.pappletClass.isAssignableFrom(paramTys[0])) {
            start = 1; // when unifying the fields of the signature s, start against the second parameter (type) of the constructor,
                       // since the first parameter is the Processing PApplet
        } else if (paramTys.length != s.getFieldCount()) {
            return null;   // no way to unify because the number of parameters is different
        }
        
        ArgSpec[] newArgs = new ArgSpec[s.getFieldCount()]; // for the specialized signature
        for (int i = start; i < paramTys.length; i++) {
            Class<?> ci = paramTys[i];
            ISig si = s.getFieldSig(i);
            try {
                ISig siSpec = si.apply(new SigClassUnifier(ci));
                newArgs[i] = new ArgSpec(s.getFieldName(i), siSpec);
            } catch (SignatureUnificationException e) {  // something didn't unify...
                return null;
            }
        }    
        
        CompSig<C> newS = new CompSig<C>(s.getAssociatedClass(), newArgs);
        return newS;
    }


    /**
     * Attempts to apply this unifier and returns whether the result
     * was successful (no exceptions occurred).
     * @param s the signature to unify with
     * @return true if unification succeeded; false if exception occurred
     */
    public boolean unifiesWith(ISig s) {
        try {
            s.apply(this);  // see if unification works with no exceptions
        } catch (SignatureUnificationException e) {
            return false;
        }
        return true;        
    }
    
   
    @Override
    public ISig defaultVisit(ISig s) {
        throw exception(SignatureUnificationException.class, "scunify:unknown-sig", s); 
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
                             s.toString(), c.getSimpleName());
    }

    @Override
    public ISig visit(CompSig<?> s) {
        if (!s.getAssociatedClass().equals(c)) {
            throw exception(SignatureUnificationException.class, "scunify:comp-classmismatch",
                            s.toString(), c.getSimpleName());
        }

        try {
            ConstructorSigPair<?> cp = findConstructor(s);
            return cp.sig;
        } catch (SignatureUnificationException e) {
            throw exception(SignatureUnificationException.class, "scunify:unify-fail/bc", s, c.getSimpleName(), e.getMessage());
        }
    }

    @Override
    public ISig visit(ListSig s) {
        if (c.isArray()) {
            Class<?> ce = c.getComponentType();
            try {
                return new ListSig(s.getElemType().apply(new SigClassUnifier(ce, false)));
            } catch (SignatureUnificationException e) {
                throw exception(SignatureUnificationException.class, "scunify:unify-fail/bc", s, c.getSimpleName(), e.getMessage());
            }
        } else if (c == ArrayList.class) {
            // not possible to reflectively get element type of arraylist
            return s;
        } else {
            throw exception(SignatureUnificationException.class, "scunify:unify-fail", s, c.getSimpleName());
        }
    }

}
