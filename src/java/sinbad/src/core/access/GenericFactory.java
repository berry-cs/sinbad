package core.access;

import core.log.Errors;
import core.schema.ISchema;

/**
 * Implements a skeleton of a data access factory with no 
 * options and that does basic bookkeeping of a schema
 */

public abstract class GenericFactory implements IDataAccessFactory {
    
    protected ISchema schema = null;

    /** 
     * Ignores options
     */
    @Override
    public IDataAccessFactory setOption(String option, String value) {
        return this;
    }

    @Override
    public String[] getOptions() {
        return new String[] {};
    }

    @Override
    public String getOption(String option) {
        throw Errors.exception(DataAccessException.class, "da:no-such-option", option);
    }
    
    @Override
    public IDataAccessFactory setSchema(ISchema schema) {
        this.schema = schema;
        return this;
    }
    
    @Override
    public boolean hasSchema() {
        return this.schema != null;
    }

}
