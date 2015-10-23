package core.access;

import java.io.InputStream;

public interface IDataAccessFactory {
    /**
     * Set a parameter of this factory. The available
     * options are specific to each data access type.
     */
    void setOption(String option, String value);
    
    /**
     * Create a new instance of a data access object.
     */
    IDataAccess newInstance(InputStream is);
}
