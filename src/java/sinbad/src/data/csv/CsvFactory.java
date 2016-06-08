package data.csv;

import java.io.InputStream;
import java.io.StringReader;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import core.access.DataAccessException;
import core.access.IDataAccessFactory;
import core.log.Errors;
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
    public String[] getOptions() {
        return new String[] { "header (optional)", "delimiter (default: ',')", "streaming (default: false)" };
    }
    
    @Override
    public String getOption(String option) {
        if ("header".equals(option)) {
            return String.join(",", this.header);
        } else if ("delimiter".equals(option)) {
            return ""+this.delimiter;
        } else if ("streaming".equals(option)) {
            return ""+this.streaming;
        } else {
            throw Errors.exception(DataAccessException.class, "da:no-such-option", option);
        }
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
