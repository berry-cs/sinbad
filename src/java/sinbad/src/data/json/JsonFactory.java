package data.json;

import java.io.InputStream;

import core.access.DataAccessException;
import core.access.GenericFactory;
import core.access.IDataAccess;
import core.log.Errors;

public class JsonFactory extends GenericFactory {

    private int skipChars = 0;  // number of initial characters to skip in the input stream
    
    @Override
    public IDataAccess newInstance(InputStream is) {
        
        JsonDataAccess da =  new JsonDataAccess(is, skipChars);

        if (this.schema != null) { 
            da.setSchema(schema);
        }
        return da;
    }


    @Override
    public String[] getOptions() {
        return new String[] { "skipchars" };
    }
    

    @Override
    public String getOption(String option) {
        if ("skipchars".equals(option)) {
            return ""+this.skipChars;
        } else {
            throw Errors.exception(DataAccessException.class, "da:no-such-option", option);
        }
    }
    
    @Override
    public JsonFactory setOption(String option, String value) {
        if ("skipchars".equals(option)) {
            skipChars = Integer.parseInt(value);
        } 
        return this;
    }
}
