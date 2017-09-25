package core.sig;

import java.util.HashMap;

/**
 * Represents a primitive Java type (including String). Objects of this
 * class cannot be instantiated. Instead, singleton objects are accessible
 * as constants.
 */
public class PrimSig implements ISig {
    
	public static final PrimSig BOOLEAN_SIG = new PrimSig("boolean", false);
	public static final PrimSig BYTE_SIG = new PrimSig("byte", 0);
	public static final PrimSig CHAR_SIG = new PrimSig("char", 0);
	public static final PrimSig DOUBLE_SIG = new PrimSig("double", 0.0);
	public static final PrimSig FLOAT_SIG = new PrimSig("float", 0.0f);
	public static final PrimSig INT_SIG = new PrimSig("int", 0);
	public static final PrimSig LONG_SIG = new PrimSig("long", 0);
	public static final PrimSig STRING_SIG = new PrimSig("String", "");
	public static final PrimSig WILDCARD_SIG = new PrimSig("?", null);
	
	private static HashMap<Class<?>,PrimSig> ctos = setupClassSigMap();
	
	private String name;
	private Object nullValue; // value to use if a field is parsed null
	
	protected PrimSig(String name, Object nullValue) {
		this.name = name;
		this.nullValue = nullValue;
	}
	
	/* 
	 * Sets up a mapping between Java Class objects and their
	 * canonical primitive signature
	 */
	private static HashMap<Class<?>, PrimSig> setupClassSigMap() {
        ctos = new HashMap<Class<?>, PrimSig>();
        ctos.put(Boolean.class, BOOLEAN_SIG);
        ctos.put(boolean.class, BOOLEAN_SIG);
        ctos.put(Byte.class, BYTE_SIG);
        ctos.put(byte.class, BYTE_SIG);
        ctos.put(Character.class, CHAR_SIG);
        ctos.put(char.class, CHAR_SIG);
        ctos.put(Integer.class, INT_SIG);
        ctos.put(int.class, INT_SIG);
        ctos.put(long.class, LONG_SIG);
        ctos.put(Long.class, LONG_SIG);
        ctos.put(Double.class, DOUBLE_SIG);
        ctos.put(double.class, DOUBLE_SIG);
        ctos.put(float.class, FLOAT_SIG);
        ctos.put(Float.class, FLOAT_SIG);
        ctos.put(String.class, STRING_SIG);
        return ctos;
    }
	
	/** 
	 * Produces the appropriate primitive signature for the given Java class
	 * object (assuming it is for one of the primitive wrapper classes)
	 * @param c a Java primitive (or wrapper) type class object
	 * @return a corresponding primitive signature
	 */
	public static PrimSig primSigFor(Class<?> c) {
	    return ctos.get(c);
	}

	/**
	 * Applies the given visitor to this primitive signature
	 * @return the result of the visitor operation
	 */
    @Override
    public <A> A apply(ISigVisitor<A> sv) {
        return sv.visit(this);
    }
    
    /**
     * Produces the name of the primitive type represented by this object 
     * @return the name of a primitive type
     */
    public String getName() {
        return name; 
    }
    
    /**
     * Produces the default value of this primitive type (in case
     * data is missing during binding)
     * @return primitive data value (in a wrapper class as appropriate)
     */
    public Object getNullValue() {
        return nullValue;
    }

    /** 
     * Produces a string rendering of this primitive signature
     */
    @Override
    public String toString() {
        return getName();
    }
	
}