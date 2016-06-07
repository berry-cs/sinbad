package data.csv;

import java.io.InputStream;
import java.io.StringReader;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import core.access.IDataAccessFactory;
import core.schema.ISchema;

public class CsvFactory implements IDataAccessFactory {

    private String[] header; // if null, should be read from the input
    private char delimiter = ',';  // default is comma
    private boolean streaming = false;
    private ISchema schema = null;
    
    public CsvFactory() {
    }

    public CsvFactory setOption(String option) {
        return this.setOption(option, null);
    }

    @Override
    public CsvFactory setOption(String option, String value) {
        if ("header".equals(option)) {
            CsvParserSettings sts = new CsvParserSettings();
            sts.setLineSeparatorDetectionEnabled(true);
            //sts.getFormat().setDelimiter(this.delimiter);
            CsvParser q = new CsvParser(sts);
            q.beginParsing(new StringReader(value));
            this.header = q.parseNext();
            q.stopParsing();
        } else if ("delimiter".equals(option)) {
            delimiter = value.charAt(0);
        } else if ("streaming".equals(option)) {
            streaming = true;
        }
        return this;
    }
    
    public CsvFactory setSchema(ISchema schema) {
        this.schema = schema;
        return this;
    }
    
    public boolean hasSchema() {
        return this.schema != null;
    }

    @Override
    public CsvDataAccess newInstance(InputStream is) {
        CsvDataAccess da =  new CsvDataAccess(is, header, delimiter, streaming);
        if (this.schema != null) { 
            da.setSchema(schema);
        }
        return da;
    }

}
