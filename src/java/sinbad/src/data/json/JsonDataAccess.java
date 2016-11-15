package data.json;

import java.io.InputStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.json.JSONPointer;
import org.json.JSONArray;
import org.json.JSONPointerException;
import org.json.JSONTokener;

import core.access.FailAccess;
import core.access.IDataAccess;
import core.schema.ISchema;
import data.xml.XML;
import data.xml.XmlDataAccess;
import data.xml.XmlSchemaBuilder;

public class JsonDataAccess extends FailAccess {
    private Object data;  // could be a Boolean, Double, Integer, Long, String, JSONArray, JSONObject, or null
    private ISchema schema;

    public JsonDataAccess(InputStream is, int skipChars) {
        JSONTokener jt = new JSONTokener(is);
        for (int i = 0; i < skipChars; i++) { jt.next(); }  // allow skipping past some initial characters
        data = jt.nextValue();
    }
    
    private JsonDataAccess(Object data) {
        this.data = data;
    }
    
    /**
     * Provide a predefined schema for this data set
     */
    public void setSchema(ISchema schema) {
        this.schema = schema;
    }
    
    @Override
    public ISchema getSchema() {
        if (this.schema == null) {
            this.schema = JsonSchemaBuilder.inferSchema(data);
        } 
        return this.schema;
    }
    
    @Override
    public String getContents() {
        if (data == JSONObject.NULL) 
            return "";
        else 
            return data.toString();
    }
    
    @Override
    public IDataAccess get(String path) {
        if (path == null) {
            return super.get(path);
        }
        
        try {
            if (!path.startsWith("/")) { path = "/" + path; }
            Object result = new JSONPointer(path).queryFrom(data);
            return new JsonDataAccess(result);
        } catch (JSONPointerException e) {
            return super.get(path + " (" + e + ")");
        }
    }

    @Override
    public IDataAccess get(String path, int i) {
        if (path == null) {
            if (data instanceof JSONArray) {
                JSONArray arr = (JSONArray) data;
                if (i < arr.length()) {
                    return new JsonDataAccess(arr.get(i));
                } 
            }
        } else {
            try {
                if (!path.startsWith("/")) { path = "/" + path; }
                Object result = new JSONPointer(path).queryFrom(data);
                return new JsonDataAccess(result).get(null, i);  // try to select the i'th element
            } catch (JSONPointerException e) {
                return super.get(path + " (" + e + ")");
            }
        }

        return super.get(path);
    }
    
    @Override
    public Stream<IDataAccess> getAll(String path) {
        if (path == null) {
            if (data instanceof JSONArray) {
                JSONArray arr = (JSONArray) data;
                return IntStream.range(0,  arr.length()).mapToObj(i -> new JsonDataAccess(arr.get(i)));
            }
        } else {
            try {
                if (!path.startsWith("/")) { path = "/" + path; }
                Object result = new JSONPointer(path).queryFrom(data);
                return new JsonDataAccess(result).getAll(null);
            } catch (JSONPointerException e) {
                return super.getAll(path + " (" + e + ")");
            }
        }

        return super.getAll(path);
    }
}
