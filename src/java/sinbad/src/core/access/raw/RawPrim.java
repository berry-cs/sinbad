package core.access.raw;

import core.access.*;
import core.schema.*;

/**
 * Provides a simple, in-memory data access object around
 * a primitive data element.
 *
 */
public class RawPrim extends FailAccess implements IRawAccess {
    private String data;
    
    public RawPrim(String data) {
        this.data = data;
    }
    
    /** 
     * Access the contents of the current data element
     * 
     * @return the entire contents of this data element
     */
    @Override
    public String getContents() {
        return this.data;
    }
    
    public String toString() {
        return String.format("\"%s\"", this.data);
    }
    
    @Override
    public ISchema getSchema() {
        return this.getSchema(null);
    }

    @Override
    public ISchema getSchema(String basePath) {
        return new PrimSchema(basePath);
    }
}