package core.access.raw;

import core.access.*;

/**
 * Provides a simple, in-memory data access object around
 * a structure of fields and sub-objects.
 *
 */
public class RawStruct extends FailAccess {
    private RawStructField[] flds;
    
    public RawStruct(RawStructField ... flds) {
        this.flds = flds;
    }
    
    /**
     * Access a data element that matches path
     * 
     * @param path the path to a data element
     * @return a data access object for the specified element
     */
    @Override
    public IDataAccess get(String path) {
        for (RawStructField f : flds) {
            if (f.name.equals(path)) 
                return f.da;
        }
        return super.get(path);     // this will cause an exception
    }
}