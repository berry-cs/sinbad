package core.data;

import java.util.Iterator;
import java.util.stream.Stream;

import core.access.IDataAccess;
import core.log.Errors;
import core.ops.IDataOp;
import core.ops.SchemaPrettyPrint;
import core.ops.SchemaSigUnifier;
import core.ops.SigClassUnifier;
import core.schema.CompSchema;
import core.schema.ISchema;
import core.schema.ISchemaVisitor;
import core.schema.ListSchema;
import core.schema.PrimSchema;
import core.sig.ISig;
import core.sig.SigUtils;

public class GenDataSourceIterator implements DataSourceIterator {

    private final DataSource dataSource;
    private Iterator<IDataAccess> iter;  // stream of data for this iterator 
    private ISchema eltSch;              // the schema of each element of data in the stream
    private IDataAccess curData;         // stores the current data element

    /**
     * Build a data source iterator for the given a data source; assumes the data source is loaded
     * @param orig
     */
    public GenDataSourceIterator(DataSource dataSource) {
        if (!dataSource.hasData())
            throw Errors.exception(DataSourceException.class, "ds:no-data", dataSource.getName());
        
        this.dataSource = dataSource;
        
        reset();
    }

    
    public DataSourceIterator reset() {
        IDataAccess orig = this.dataSource.getDataAccess();
        
        orig.getSchema().apply(new ISchemaVisitor<Void>() {
            public Void visit(ListSchema s) {
                iter = orig.getAll(s.getPath()).iterator();
                eltSch = s.getElementSchema();
                return null;
            }

            public Void defaultVisit(ISchema sch) {
                iter = Stream.of(orig).iterator();
                eltSch = sch;
                return null;
            }
            public Void visit(PrimSchema sch) { return defaultVisit(sch); }
            public Void visit(CompSchema sch) { return defaultVisit(sch); }

        });
        
        if (iter.hasNext()) loadNext();  // initialize the first piece of data
        else curData = null;

        return this;
    }
    
    
    public boolean hasData() {
        return curData != null;
    }

    public DataSourceIterator loadNext() {
        if (iter.hasNext()) curData = iter.next();
        else curData = null;
        return this;
    }

    public <T> T fetch(Class<T> cls, String... keys) {
        if (!this.hasData())
            throw Errors.exception(DataSourceException.class, "ds:no-data", this.dataSource.getName());
            
        ISig presig = SigUtils.buildCompSig(cls, keys);
        ISig sig = presig.apply(new SigClassUnifier(cls));
        IDataOp<T> op = SchemaSigUnifier.unifyWith(eltSch, sig);
        return op.apply(this.curData);
    }
    
    public <T> T fetch(String clsName, String... keys) {
        return fetch(SigUtils.classFor(clsName), keys);
    }

    public boolean fetchBoolean(String key) { return fetch(Boolean.class, key); }
    public byte fetchByte(String key) { return fetch(Byte.class, key); }
    public char fetchChar(String key) { return fetch(Character.class, key); }
    public double fetchDouble(String key) { return fetch(Double.class, key); }
    public float fetchFloat(String key) { return fetch(Float.class, key); }
    public int fetchInt(String key) { return fetch(Integer.class, key); }
    public String fetchString(String key) { return fetch(String.class, key); }
    
    public String usageString() {
        String s = "\nThe following data is available through iterator for: " + this.dataSource.getName() + "\n";
        s += eltSch.apply(new SchemaPrettyPrint(3, true)) + "\n";
        return s;
    }

}
