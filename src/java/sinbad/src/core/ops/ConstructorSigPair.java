package core.ops;

import java.lang.reflect.Constructor;

import core.sig.CompSig;

/**
 * Used to package multiple return values from <code>findConstructor</code>.
 */
public class ConstructorSigPair<C> {
    public final Constructor<C> constructor;
    public final CompSig<C> sig;

    public ConstructorSigPair(Constructor<C> constructor, CompSig<C> sig) {
        super();
        this.constructor = constructor;
        this.sig = sig;
    }
}