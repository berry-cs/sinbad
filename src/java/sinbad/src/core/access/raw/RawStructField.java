package core.access.raw;

import core.access.IDataAccess;

/**
 * A field (name + data) in a raw structured data access object
 * @author nhamid
 *
 */
public class RawStructField {
    String name;
    IDataAccess da;

    public RawStructField(String name, IDataAccess da) {
        this.name = name;
        this.da = da;
    }
}