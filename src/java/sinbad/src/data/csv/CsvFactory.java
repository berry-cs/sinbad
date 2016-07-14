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
    private char quote = '"';
    private boolean streaming = false;
    private int skipRows = 0;   // number of initial rows to skip
    private ISchema schema = null;
    
    public CsvFactory() {
    }

    public CsvFactory setOption(String option) {
        return this.setOption(option, null);
    }

    @Override
    public String[] getOptions() {
        return new String[] { "header", "delimiter", "streaming", "skiprows", "quote" };
    }
    
    @Override
    public String getOption(String option) {
        if ("header".equals(option)) {
            if (this.header == null) return null;
            else return String.join(",", this.header);
        } else if ("delimiter".equals(option)) {
            return ""+this.delimiter;
        } else if ("streaming".equals(option)) {
            return ""+this.streaming;
        } else if ("skiprows".equals(option)) {
            return ""+this.skipRows;
        } else if ("quote".equals(option)) {
            return ""+this.quote;
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
        } else if ("skiprows".equals(option)) {
            skipRows = Integer.parseInt(value);
        } else if ("quote".equals(option)) {
            quote = value.charAt(0);
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
        CsvDataAccess da =  new CsvDataAccess(is, header, delimiter, quote, streaming, skipRows);
        if (this.schema != null) { 
            da.setSchema(schema);
        }
        return da;
    }

}
