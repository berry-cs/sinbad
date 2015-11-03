package data.raw;

import org.apache.commons.lang3.StringUtils;

import core.access.*;
import core.schema.*;

/**
 * Provides a simple, in-memory data access object around
 * a structure of fields and sub-objects.
 *
 */
public class RawStruct extends FailAccess implements IRawAccess {
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
    
    public String toString() {
        return "{" + StringUtils.join(flds, ", ") + "}";
    }

    @Override
    public ISchema getSchema() {
        return this.getSchema(null);
    }

    @Override
    public ISchema getSchema(String basePath) {
        CompField[] cfs = new CompField[flds.length];
        for (int i = 0; i < flds.length; i++) {
            RawStructField f = flds[i];
            cfs[i] = new CompField(f.name, f.da.getSchema(f.name));
        }
        return new CompSchema(basePath, cfs);
    }
    
}