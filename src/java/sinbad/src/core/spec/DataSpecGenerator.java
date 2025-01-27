/**
 * Produces a data specification from a given data source
 */

package core.spec;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;
import org.json.JSONWriter;

import core.data.DataSource;

public class DataSpecGenerator {
    private DataSource ds;

    public DataSpecGenerator(DataSource ds) {
        this.ds = ds;
    }
    
    public void saveSpec(File f) {
        try {
            FileWriter fw = new FileWriter(f);
            fw.write(this.getJSONSpec(3));
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String getJSONSpec() {
        return this.getJSONSpec(0);
    }
    
    public String getJSONSpec(int indentFactor) {
        JSONObject jobj = new JSONObject(this.ds.export());
        /*JSONWriter.keyOrder = new String[] { "type", "path", "name", "format", "infourl", "key", 
                                             "value", "description", "required", "elements", "fields", 
                                             "options", "params", "cache", "schema" };*/
        return jobj.toString(indentFactor);
    }

    public String toString() {
        return getJSONSpec();
    }
    
    public String toString(int indentFactor) {
        return getJSONSpec(indentFactor);
    }
}
