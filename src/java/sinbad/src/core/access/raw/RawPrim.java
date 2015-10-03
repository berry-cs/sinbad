package core.access.raw;

import core.access.*;

/**
 * Provides a simple, in-memory data access object around
 * a primitive data element.
 *
 */
public class RawPrim extends FailAccess {
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
}