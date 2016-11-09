package core.sig;

import java.util.Arrays;

/**
 * Represents the signature of a constructor for a compound data
 * type (i.e. a class)
 * In addition to the order of the types of the fields (corresponding
 * to constructor parameters), this object also captures the names
 * of the fields, in order to allow mapping from names of fields
 * in a data source to the parameters positions of constructor
 * 
 * @param <C> a class type for which this represents a constructor signature 
 */
public class CompSig<C> implements ISig {
	private Class<C> cls;
	private ArgSpec[] args;
	
	/**
	 * Create a signature corresponding to a constructor of some class with
	 * a particular set of parameters
	 * 
     * @param cls the class associated with this signature
     * @param args the parameters of the constructor that this signature represents
     */
    public CompSig(Class<C> cls, ArgSpec ... args) {
        super();
        this.cls = cls;
        this.args = args;
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
     * Produces the class that this signature is associated with
     * @return the class of this signature's constructor
     */
    public Class<C> getAssociatedClass() { 
        return cls;
    }
    
    /**
     * Produces a signature for one of the parameters of the
     * constructor represented by this signature
     * @param i the index of the parameter to return
     * @return a signature
     */
    public ISig getFieldSig(int i) {
        return args[i].getType();
    }
    
    /**
     * Produces a field name for one of the parameters of the
     * constructor represented by this signature
     * @param i the index of the parameter to return
     * @return a name
     */
    public String getFieldName(int i) {
        return args[i].getName();
    }
    
    /**
     * Produces how many parameters there are for this
     * constructor represented by this signature
     * @return a number of parameters, or fields
     */
    public int getFieldCount() {
        return args.length;
    }
    
    /**
     * Produces a new signature resulting from removing
     * a given prefix from every field's name of this signature
     */
    public CompSig<C> trimPrefix(String prefix) {
        ArgSpec[] argsCopy = new ArgSpec[args.length];
        for (int i = 0; i < args.length; i++) {
            int k = prefix.length();
            if (prefix.equals(args[i].name + "/")) { k--; }
            argsCopy[i] = new ArgSpec(args[i].name.substring(k), 
                                      args[i].type);
        }
        return new CompSig<C>(cls, argsCopy);        
    }
	
    /**
     * Produces a string representation of this compound signature
     */
    public String toString() {
        String m = cls.getName() + "{";
        boolean firstDone = false;
        for (ArgSpec a : args) {
            if (firstDone) { m += ", "; } else { firstDone = true; }
            m += (a.getName() + ": " + a.getType());
        }
        m += "}";
        return m;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(args);
        result = prime * result + ((cls == null) ? 0 : cls.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof CompSig))
            return false;
        CompSig other = (CompSig) obj;
        if (!Arrays.equals(args, other.args))
            return false;
        if (cls == null) {
            if (other.cls != null)
                return false;
        } else if (!cls.getCanonicalName().equals(other.cls.getCanonicalName()))
            return false;
        return true;
    }
}