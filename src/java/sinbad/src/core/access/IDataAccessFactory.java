package core.access;

import java.io.InputStream;

import core.schema.ISchema;

public interface IDataAccessFactory {
    /**
     * Set an option of this factory. The available
     * options are specific to each data access type.
     * @return this factory
     */
    IDataAccessFactory setOption(String option, String value);
    
    /**
     * Produce a list of option keys for this data access type
     * @return a list of option keys (preferably indicating optional/required/default/possible values)
     */
    String[] getOptions();
    
    /**
     * Produce the value for the given option of this factory.
     */
    String getOption(String option);
    
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
