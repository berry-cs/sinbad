package data.xml;

import java.io.InputStream;

import core.access.IDataAccess;
import core.access.IDataAccessFactory;
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
    public IDataAccess newInstance(InputStream is) {
        XmlDataAccess da =  new XmlDataAccess(is);
        if (this.schema != null) { 
            da.setSchema(schema);
        }
        return da;
    }
}
