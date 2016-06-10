package data.json;

import java.io.InputStream;

import core.access.GenericFactory;
import core.access.IDataAccess;
import data.xml.XmlDataAccess;

public class JsonFactory extends GenericFactory {

    @Override
    public IDataAccess newInstance(InputStream is) {
        JsonDataAccess da =  new JsonDataAccess(is);
        if (this.schema != null) { 
            da.setSchema(schema);
        }
        return da;
    }

}
