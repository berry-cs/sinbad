/**
 * 
 */
package data.csv;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import core.access.*;
import core.ops.*;
import core.schema.*;
import core.sig.*;
import core.util.FileLoader;
import core.util.IOUtil;

import static core.log.Errors.*;

/**
 * A delimiter-separated (usually comma-separated) data access object.
 */
public class CsvDataAccess extends FailAccess {

    private String[] header;
    private HashMap<String,Integer> headerIndex;
    private char delimiter;
    private CsvParser p;
    private List<String[]> allRows;
    private boolean streaming;   // load rows on demand for getAll()
    private ISchema schema;
       
    public CsvDataAccess(InputStream is, String[] header, char delimiter, boolean streaming, int skipRows) {
        this.header = header;
        this.delimiter = delimiter;
        this.allRows = new ArrayList<String[]>();
        this.streaming = streaming;
        
        CsvParserSettings sts = new CsvParserSettings();
        sts.setLineSeparatorDetectionEnabled(true);
        sts.getFormat().setDelimiter(this.delimiter);
        
        p = new CsvParser(sts);
        //System.err.println(p.parseAll(new InputStreamReader(is)));
        p.beginParsing(new InputStreamReader(new BufferedInputStream(is)));
        
        for (int i = 0; i < skipRows; i++) {
            p.parseNext();
        }
        
        if (this.header == null) {
            this.header = p.parseNext();
        }
        if (this.header == null) {
            throw exception(DataAccessException.class, "da:construct", "missing header");
        }
        
        this.headerIndex = new HashMap<String, Integer>();
        for (int i = 0; i < this.header.length; i++) {
            if (this.header[i] != null) {
                this.headerIndex.put(this.header[i], i);
            }
        }
        
        this.schema = null; // build it later on demand
    }
    
    /**
     * Produce a schema for this data set 
     */
    @Override
    public ISchema getSchema() {
        if (this.schema == null) {
            this.schema = buildSchema();
        }
        return this.schema;
    }
    
    /**
     * Provide a predefined schema for this data set
     */
    public void setSchema(ISchema schema) {
        this.schema = schema;
    }
    
    private ISchema buildSchema() {
        int nonNull = 0;
        for (String h : this.header) {
            if (h != null) nonNull++;
        }
        
        CompField[] fields = new CompField[nonNull];
        for (int i = 0; i < this.header.length; i++) {
            if (this.header[i] != null) {
                PrimSchema ps = new PrimSchema(this.header[i]);  // basepath
                fields[i] = new CompField(this.header[i], ps);
            }
        }
        return new ListSchema(new CompSchema(fields));
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
        if (!streaming) {
            // read in rest of rows
            String[] nextRow;
            while ( (nextRow = p.parseNext()) != null ) {
                allRows.add(nextRow);
            }
            return allRows.stream().map((row) -> new StringRowAccess(row));
        } else {
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

    
    public static void main(String[] args) {
        //InputStream in = IOUtil.createInput("https://raw.githubusercontent.com/jpatokal/openflights/master/data/routes.dat");
         InputStream in = new FileLoader().createInput("/Users/nhamid/Downloads/routes.dat");
         CsvDataAccess csv = (CsvDataAccess)new CsvFactory().setOption("header", "Airline,ID,Source Airport,Source ID,Dest Airport,Dest ID,Code Share,Stops,Equipment")
                 .newInstance(in);
         long millis = System.currentTimeMillis();
         Stream<IDataAccess> s = csv.getAll(null);
         System.out.println(System.currentTimeMillis() - millis);
         IDataAccess[] rows = s.toArray(IDataAccess[]::new);
         System.out.println(rows.length);
         System.out.println(rows[10].get("Dest Airport").getContents());
         ISchema sch = csv.getSchema();       
         ISig sig = new CompSig<String>(String.class, new ArgSpec("Dest Airport", PrimSig.STRING_SIG));
         System.out.println(sch);
         System.out.println(sig);
         
         IDataOp<?> dop = SchemaSigUnifier.unifyWith(sch, sig);
         System.out.println(dop);
         
         String e = (String)dop.apply(csv);
         System.out.println(e);
     }
     


}
