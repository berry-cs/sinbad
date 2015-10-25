package data.csv;

import java.io.InputStream;
import java.io.StringReader;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import core.access.IDataAccessFactory;

public class CsvFactory implements IDataAccessFactory {

    private String[] header; // if null, should be read from the input
    private char delimiter = ',';  // default is comma
    private boolean streaming = false;
    
    public CsvFactory() {
    }

    @Override
    public CsvFactory setOption(String option, String value) {
        if ("header".equals(option)) {
            CsvParserSettings sts = new CsvParserSettings();
            sts.setLineSeparatorDetectionEnabled(true);
            sts.getFormat().setDelimiter(this.delimiter);
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

    @Override
    public CsvDataSource newInstance(InputStream is) {
        return new CsvDataSource(is, header, delimiter, streaming);
    }

}
