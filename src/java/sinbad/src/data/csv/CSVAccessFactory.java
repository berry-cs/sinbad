package data.csv;

import java.io.InputStream;
import java.io.StringReader;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import core.access.IDataAccess;
import core.access.IDataAccessFactory;

public class CSVAccessFactory implements IDataAccessFactory {

    private String[] header; // if null, should be read from the input
    private char delimiter = ',';  // default is comma
    private boolean streaming = false;
    
    public CSVAccessFactory() {
    }

    @Override
    public IDataAccessFactory setOption(String option, String value) {
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
    public IDataAccess newInstance(InputStream is) {
        return new CSVAccess(is, header, delimiter, streaming);
    }

}
