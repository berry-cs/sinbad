package core.sig;


/**
 * A signature approximately represents a Java type and is used
 * to guide the instantiation of Java objects being bound to a 
 * data source. 
 * 
 * @author Nadeem Abdul Hamid
 *
 */
public interface ISig {
    /**
     * Hook for the visitor pattern
     * @param sv a visitor object
     * @return the result of the visitor operation
     */
    public <A> A apply(ISigVisitor<A> sv);
}
