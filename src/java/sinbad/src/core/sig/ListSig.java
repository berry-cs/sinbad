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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((elemType == null) ? 0 : elemType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ListSig))
            return false;
        ListSig other = (ListSig) obj;
        if (elemType == null) {
            if (other.elemType != null)
                return false;
        } else if (!elemType.equals(other.elemType))
            return false;
        return true;
    }
}
