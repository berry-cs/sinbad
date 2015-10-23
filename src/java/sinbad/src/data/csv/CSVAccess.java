/**
 * 
 */
package data.csv;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.*;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import core.access.DataAccessException;
import core.access.FailAccess;
import core.access.IDataAccess;

import static core.log.Errors.*;

/**
 * A delimiter-separated (usually comma-separated) data access object.
 */
public class CSVAccess extends FailAccess {

    private String[] header;
    private HashMap<String,Integer> headerIndex;
    private char delimiter;
    private CsvParser p;
    List<String[]> allRows;
       
    public CSVAccess(InputStream is, String[] header, char delimiter) {
        this.header = header;
        this.delimiter = delimiter;
        this.allRows = new ArrayList<String[]>();
        
        CsvParserSettings sts = new CsvParserSettings();
        sts.setLineSeparatorDetectionEnabled(true);
        sts.getFormat().setDelimiter(this.delimiter);
        p = new CsvParser(sts);
        //System.err.println(p.parseAll(new InputStreamReader(is)));
        p.beginParsing(new InputStreamReader(is));
        if (this.header == null) {
            this.header = p.parseNext();
        }
        
        this.headerIndex = new HashMap<String, Integer>();
        for (int i = 0; i < this.header.length; i++) {
            this.headerIndex.put(this.header[i], i);
        }
    }

    /* 
     * Get the i'th row in the data. It parses forward as necessary,
     *  adding rows to 'allRows' until it has reached the i'th one.
     *  
     *  @return a row, or null if error (or end of input reached)
     */
    private String[] getRow(int i) {      
        while (i >= allRows.size()) {
            String[] nextRow = p.parseNext();
            if (nextRow == null) return null;
            allRows.add(nextRow);
        }
        return allRows.get(i);        
    }
    

    /* (non-Javadoc)
     * @see core.access.IDataAccess#get(java.lang.String, int)
     */
    /**
     * @param path is ignored by this data access object
     */
    @Override
    public IDataAccess get(String path, int i) {
        return new RowAccess(i);
    }

    private class RowAccess extends FailAccess {
        int i;
        
        public RowAccess(int i) {
            this.i = i;
        }
        
        @Override
        public IDataAccess get(String path) {
            if (!headerIndex.containsKey(path)) {
                throw exception(DataAccessException.class, "da:get-path", path);
            }
            return new CellAccess(path);
        }
        
        class CellAccess extends FailAccess {
            String field;
            
            public CellAccess(String field) {
                this.field = field;
            }
            
            public String getContents() {
                String[] row = getRow(i);
                if (row == null) {
                    throw exception(DataAccessException.class, "da:index", i);
                }
                int j = headerIndex.get(field);
                if (j >= row.length) {
                    throw exception(DataAccessException.class, "da:index", j);
                }
                if (row[j] == null) {
                    return "";
                } else {
                    return row[j];
                }
            }
        }
    }
    

    /* (non-Javadoc)
     * @see core.access.IDataAccess#getAll(java.lang.String)
     */
    @Override
    /**
     * @param path is ignored by this data access object
     */
    public Stream<IDataAccess> getAll(String path) {
        Iterator<IDataAccess> source = new Iterator<IDataAccess>() {
            boolean done = false;
            String[] readAhead = null;
            
            @Override
            public boolean hasNext() {
                this.readAhead = p.parseNext();
                if (this.readAhead == null) done = true;
                return (!done);
            }

            @Override
            public IDataAccess next() {
                String[] nextRow;
                if (done) {
                    throw new NoSuchElementException(); 
                }
                if (this.readAhead != null) {
                    nextRow = this.readAhead;
                } else {
                    nextRow = p.parseNext();
                }
                if (nextRow == null) {
                    throw new NoSuchElementException(); 
                }
                return new StringRowAccess(nextRow);
            }
            
        };
        Iterable<IDataAccess> iterable = () -> source;
        return StreamSupport.stream(iterable.spliterator(), false);
    }
    
    
    private class StringRowAccess extends FailAccess {
        String[] row;
        
        public StringRowAccess(String[] row) {
            this.row = row;
        }
        
        @Override
        public IDataAccess get(String path) {
            if (!headerIndex.containsKey(path)) {
                throw exception(DataAccessException.class, "da:get-path", path);
            }
            return new CellAccess(path);
        }
        
        class CellAccess extends FailAccess {
            String field;
            
            public CellAccess(String field) {
                this.field = field;
            }
            
            public String getContents() {
                int j = headerIndex.get(field);
                if (j >= row.length) {
                    throw exception(DataAccessException.class, "da:index", j);
                }
                if (row[j] == null) {
                    return "";
                } else {
                    return row[j];
                }
            }
        }
    }


}
