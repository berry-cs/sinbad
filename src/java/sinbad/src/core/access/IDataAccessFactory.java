package core.access;

import java.io.InputStream;

import core.schema.ISchema;

public interface IDataAccessFactory {
    /**
     * Set a parameter of this factory. The available
     * options are specific to each data access type.
     * @return this factory
     */
    IDataAccessFactory setOption(String option, String value);
    
    /**
     * Provide a predefined schema for the data source
     */
    IDataAccessFactory setSchema(ISchema schema);
    
    
    /**
     * Does this factory have a schema
     */
    boolean hasSchema();
    
    /**
     * Create a new instance of a data access object.
     */
    IDataAccess newInstance(InputStream is);
}
