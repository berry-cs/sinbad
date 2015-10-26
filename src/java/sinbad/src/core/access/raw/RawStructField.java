package core.access.raw;

import core.access.IDataAccess;

/**
 * A field (name + data) in a raw structured data access object
 * @author nhamid
 *
 */
public class RawStructField {
    String name;
    IRawAccess da;

    public RawStructField(String name, IRawAccess da) {
        this.name = name;
        this.da = da;
    }
    
    public String toString() {
        return name + ": " + da;
    }
}
