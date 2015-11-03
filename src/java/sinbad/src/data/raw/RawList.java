package data.raw;

import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import core.access.*;
import core.schema.*;

/**
 * Provides a simple, in-memory data access object around
 * a list of other data access objects.
 */
public class RawList extends FailAccess implements IRawAccess {
    private String eltPath;         // could be null
    private IRawAccess[] elts;
    
    public RawList(String eltPath, IRawAccess ... elts) {
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
    
    public String toString() {
        return "{" + eltPath + ": " + StringUtils.join(elts, ", " + eltPath + ": ") + "}";
    }

    @Override
    public ISchema getSchema() {
        return this.getSchema(null);
    }

    @Override
    public ISchema getSchema(String basePath) {
        return new ListSchema(basePath, elts[0].getSchema(eltPath));
    }
}