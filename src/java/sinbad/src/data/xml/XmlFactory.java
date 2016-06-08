package data.xml;

import java.io.InputStream;

import core.access.DataAccessException;
import core.access.IDataAccess;
import core.access.IDataAccessFactory;
import core.log.Errors;
import core.schema.ISchema;

public class XmlFactory implements IDataAccessFactory {

    private ISchema schema = null;
    
    @Override
    public IDataAccessFactory setOption(String option, String value) {
        return this;
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

    @Override
    public IDataAccess newInstance(InputStream is) {
        XmlDataAccess da =  new XmlDataAccess(is);
        if (this.schema != null) { 
            da.setSchema(schema);
        }
        return da;
    }

    @Override
    public String[] getOptions() {
        return new String[] {};
    }

    @Override
    public String getOption(String option) {
        throw Errors.exception(DataAccessException.class, "da:no-such-option", option);
    }
}
