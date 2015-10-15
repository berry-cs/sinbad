package core.access.raw;

import java.util.stream.Stream;
import core.access.*;

/**
 * Provides a simple, in-memory data access object around
 * a list of other data access objects.
 */
public class RawList extends FailAccess {
    private String eltPath;         // could be null
    private IDataAccess[] elts;
    
    public RawList(String eltPath, IDataAccess ... elts) {
        this.eltPath = eltPath;
        this.elts = elts;
    }
    
    /**
     * Access the i'th data element that matches path
     * 
     * @param path the path to a collection of data elements 
     * @param i index of the element to access
     * @return a data access object for the specified element
     */
    @Override
    public IDataAccess get(String path, int i) {
        if (eltPath == path || (eltPath != null && eltPath.equals(path)))
            return this.elts[i];
        else
            return super.get(path, i);
    }
    
    /**
     * Access a stream of data elementsthat match path
     * 
     * @param path the path to a collection of data elements 
     * @return a stream of data access objects for the specified elements
     */
    @Override
    public Stream<IDataAccess> getAll(String path) {
        if (eltPath == path || (eltPath != null && eltPath.equals(path)))
            return Stream.of(this.elts);
        else
            return super.getAll(path);
    }
}