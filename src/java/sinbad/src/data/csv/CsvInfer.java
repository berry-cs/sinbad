package data.csv;

import java.util.HashMap;
import java.util.Map;

import core.infer.IDataFormatInfer;

public class CsvInfer implements IDataFormatInfer {

    private HashMap<String,String> options;
    private String delim;
    
    public CsvInfer() {
        options = new HashMap<String,String>();
        this.delim = ",";
    }

    public CsvInfer(String delim) {
        this();
        options.put("delimiter", delim);
        this.delim = delim;
    }
    
    @Override
    public boolean matchedBy(String path) {
        if ((path.contains("=csv") || path.endsWith(".csv") || path.endsWith(".csv.gz") || path.endsWith(".csv.zip"))
                && this.delim.equals(",")) {
            // this is default anyway in the factory: options.put("delimiter", ","); 
            return true;
        } else if ((path.endsWith(".tsv") || path.endsWith(".tsv.gz"))
                && this.delim.equals("\t")) {
            options.put("delimiter", "\t");
            return true;
        }
        return false;
    }

    @Override
    public Map<String, String> getOptions() {
        return options;
    }

}
