package core.sig;


/**
 * Represent a list-y type of data (corresponding to an array, 
 * ArrayList, or some similar linearly organized data structure)
 */
public class ListSig implements ISig {
    /* the signature of elements of this list */
    private ISig elemType;

    /**
     * @param elemType a (non-null) signature representing the type of
     * elements of this list
     */
    public ListSig(ISig elemType) {
        super();
        this.elemType = elemType;
    }

    /**
     * Applies the given visitor to this list signature
     * @return the result of the visitor operation
     */
    @Override
    public <A> A apply(ISigVisitor<A> sv) {
        return sv.visit(this);
    }

    /**
     * Gets the signature of elements of this list
     * @return a signature
     */
    public ISig getElemType() { 
        return elemType; 
    }
    
    /** 
     * Produces a string rendering of this list signature with its element signature
     */
    public String toString() {
        return "[listof " + elemType + "]";
    }

}
