package core.data;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import core.access.*;
import core.cache.DataCacher;
import core.infer.IDataFormatInfer;
import core.log.Errors;
import core.ops.*;
import core.schema.*;
import core.sig.*;
import core.util.*;
import data.csv.*;

public class DataSource implements IDataSource {

    /* =========================================================
     * STATIC MEMBERS
     */

    private static HashMap<String,IDataSourcePlugin> plugins = new HashMap<String,IDataSourcePlugin>();
    
    static {
        plugins.put("CSV", new IDataSourcePlugin() {
            public IDataFormatInfer getInfer() { return new CsvInfer(); }
            public IDataAccessFactory getFactory() { return new CsvFactory(); } });
        plugins.put("TSV", new IDataSourcePlugin() {
            public IDataFormatInfer getInfer() { return new CsvInfer("\t"); }
            public IDataAccessFactory getFactory() { return new CsvFactory(); } });
    }
    
    public static void initializeProcessing(Object papp) {
        if (ProcessingDetector.inProcessing()) {
            ProcessingDetector.setPappletObject(papp);
        }
        else {
            System.err.println("initializeProcessing() should only be called if Processing is being used.");
        }
    }

    /**
     * Attempt to infer the format of the data at the given path and construct
     * a DataSource object for it appropriately 
     * @param path path to a data source
     * @return a prepared data source
     */
    public static DataSource connect(String path) {
        String resolvedPath = DataCacher.defaultCacher().resolvePath(path);
        if (resolvedPath == null) {
            resolvedPath = path;
        } 

        // TODO: DataSourceLoader.isValidDataSourceSpec(resolvedPath)
        for (IDataSourcePlugin dsp : plugins.values()) {
            InputStream is = IOUtil.createInput(resolvedPath);
            if (dsp.getInfer().matchedBy(path, is)) {
                if (is != null) IOUtils.closeQuietly(is);
                return connect(path, dsp);
            } 
            if (is != null) IOUtils.closeQuietly(is);
        }
        
        throw Errors.exception(DataSourceException.class, "ds:noinfer", path);
    }
    
    /** 
     * Use the specified tag (type extension) to determine a data source
     * factory to use to load the given path.
     * @param path file path or URL to data
     * @param typeExt a standard type extension (e.g. "csv", "xml", "json")
     * @return a prepared data source
     */
    public static DataSource connectAs(String path, String typeExt) {
        if (!plugins.containsKey(typeExt)) {
            throw Errors.exception(DataSourceException.class, "ds:notype", typeExt);
        } 
        return connect(path, plugins.get(typeExt));
    }
    
    public static DataSource connect(String path, IDataSourcePlugin plugin) {
        return new DataSource(path, path, plugin);
    }
    
    /**
     * Connect to a data source using the spec file at the given
     * path
     * @param path path to a spec file
     * @return a prepared data source
     */
    public static DataSource connectUsing(String path) {
        // TODO
        return null;
    }
    
    
    
    /* 
     * ========================================================= 
     * INSTANCE MEMBERS
     *
     */    
    
    protected String name;
    protected String path;  
    protected String description;
    protected String infoURL;
    
    protected HashMap<String, Param> params;
    protected ArrayList<String> paramValueKeys;
    protected HashMap<String, String> paramValues;

    protected boolean readyToLoad;
    protected boolean loaded;
    
    protected IDataSourcePlugin plugin;
    protected IDataAccess dataAccess;
    protected DataCacher cacher;
    
    protected DataSource(String name, String path, IDataSourcePlugin plugin) {
        this.name = name;
        this.path = path;
        this.description = null;
        this.infoURL = null;
        
        this.params = new HashMap<String, Param>();
        this.paramValues = new HashMap<String, String>();
        this.paramValueKeys = new ArrayList<String>();

        this.readyToLoad = false;
        this.loaded = false;
        
        this.plugin = plugin;
        this.dataAccess = null;
        this.cacher = DataCacher.defaultCacher();
    }
    
    
    /*
     * HELP
     */
    
    // TODO: make this cleaner
    public String usageString(boolean verbose) {
        String s = "-----\n";
        if (this.name != null) 
            s += "Data Source: " + this.name + "\n";
        if (description != null && !description.equals("")) s += description + "\n";
        if (infoURL != null) s += "(See " + infoURL + " for more information about this data.)\n";
        
        String[] paramKeys = params.keySet().toArray(new String[]{});
        if (paramKeys.length > 0) {
            Arrays.sort(paramKeys);
            s += "\nThe following options may/must be set on this data source:\n";
            for (String key : paramKeys) {
                Param p = params.get(key);
                String v = paramValues.get(key);
                String desc = p.getDescription();
                boolean req = p.isRequired();
                s += "   - " + key
                        + ((v==null)?" (not set)":" (currently set to: " + v + ")") 
                        + ((desc==null)?"":" : " + desc) + ((v==null && req)?" [*required]":"")
                        + "\n";
            }
        }
        
        ISchema schema = null;
        if (this.dataAccess != null) {
            schema = this.dataAccess.getSchema();
        }
        
        if (schema != null)
            s += "\nThe following data is available:\n" + schema.apply(new SchemaPrettyPrint(3, true)) + "\n";
        
        if (!this.hasData())
            s += "\n*** Data not loaded *** ... use .load()\n";
        
        s += "-----\n";
        return s;
    }
    
    public void printUsageString() {
        printUsageString(false);
    }
    
    public void printUsageString(boolean verbose) {
        System.out.println(usageString(verbose));
    }


    
  
    /*
     * ACCESSORS
     */
    
    public String getName() { return this.name; }

    public String getDescription() { return this.description; }
    
    public DataSource setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getInfoURL() { return this.infoURL; }
    
    public void setInfoURL(String infoURL) {
        this.infoURL = infoURL;
    }

    public DataSource setSchema(ISchema schema) {
        this.plugin.getFactory().setSchema(schema);
        return this;
    }

    public IDataAccess getDataAccess() {
        return this.dataAccess;
    }
    
    public IDataSource setCacheTimeout(int val) {
        this.cacher = this.cacher.updateTimeout(val);
        return this;
    }

    @Override
    public IDataSource setCacheDirectory(String path) {
        this.cacher = this.cacher.updateDirectory(path);
        return this;
    }
    
    
    /*
     * HANDLING PARAMETERS
     */
    
    public DataSource addParam(Param param) {
        if (param != null) 
            params.put(param.getKey(), param);
        return this;
    }

    public DataSource set(String op, String value) {
        if (op != null && value != null) {
            paramValues.put(op, value);
            paramValueKeys.add(op);
        }
        return this;
    }
    
    public List<String> missingParams() {
        ArrayList<String> ps = new ArrayList<String>();
        for (Param p : params.values()) {
            if (!paramValues.containsKey(p.getKey())
                    || paramValues.get(p.getKey()) == null) {
                ps.add(p.getKey());
            }
        }
        return ps;
    }
    
    public boolean readyToLoad() {
        this.readyToLoad = this.readyToLoad || missingParams().size()==0;
        return this.readyToLoad;
    }
    
    
    
    /*
     * LOADING
     */
    
    public DataSource setOption(String op, String value) {
        this.plugin.getFactory().setOption(op, value);
        return this;
    }
    
    public boolean hasData() {
        return this.loaded;
    }

    public boolean hasFields(String... keys) {
        if (!hasData()) return false;
        
        for (String key : keys) {
            try {
                fetchString(key);
            } catch (DataAccessException e) {
                return false;
            }
        }
        
        return true;        
    }
    
    public DataSource load() {
        return this.load(false);
    }
    
    public DataSource load(boolean forceReload) {
        if (!readyToLoad())
            throw Errors.exception(DataSourceException.class, "ds:notready-params", StringUtils.join(missingParams().toArray(new String[]{}), ','));

        IDataFormatInfer infer = plugin.getInfer();
        IDataAccessFactory factory = plugin.getFactory();

        String resolvedPath = this.cacher.resolvePath(this.getFullPathURL());
        if (resolvedPath == null) 
            throw Errors.exception(DataSourceException.class, "ds:no-input", path);
        InputStream is = IOUtil.createInput(resolvedPath); // first time is for running infer...
        
        infer.matchedBy(path, is); // because getOptions is only valid after matchedBy has been invoked
        Map<String,String> options = infer.getOptions();
        for (Entry<String,String> e : options.entrySet()) {
            factory.setOption(e.getKey(), e.getValue());
        }
        
        is = IOUtil.createInput(resolvedPath);  // reset input stream to beginning 
        this.dataAccess = factory.newInstance(is); // to prepare to actually parse data
        
        // TODO: load schema from cache if desired?
        this.dataAccess.getSchema();  // forces it to be build
        
        this.loaded = true;
        
        return this;
    }
    
    protected String getFullPathURL() {
        if (!readyToLoad()) 
            throw new RuntimeException("Cannot finalize path: not ready to load");

        String fullpath = this.path;
        
        // add query params to request URL...
        if (URLPrepper.isURL(this.path)) {
            URLPrepper prepper = new URLPrepper(this.path);
            for (String k : paramValueKeys) {
                Param p = params.get(k);
                if (p == null || p.getType() == ParamType.QUERY) {
                    String v = paramValues.get(k);
                    prepper.addParam(k, v);
                }
            }
            fullpath = prepper.getRequestURL();
        }
        
        // fill in substitutions
        for (String k : paramValueKeys) {
            Param p = params.get(k);
            if (p != null && p.getType() == ParamType.PATH) {
                fullpath = substParam(fullpath, k, paramValues.get(k));
            }
        }
        
        return fullpath;
    }
    
    protected String substParam(String fullpath, String key, String value) {
        return fullpath.replace((CharSequence)("@{" + key + "}"), (CharSequence)value);
    }

    
    /*
     * FETCHING DATA
     */
    
    public <T> T fetch(Class<T> cls, String... keys) {
        if (!this.hasData())
            throw Errors.exception(DataSourceException.class, "ds:no-data", this.getName());
        
        ISig sig = SigUtils.buildCompSig(cls, keys).apply(new SigClassUnifier(cls));
        ISchema sch = this.dataAccess.getSchema();
        IDataOp<T> op = SchemaSigUnifier.unifyWith(sch, sig);
        return op.apply(this.dataAccess);
    }

    public <T> ArrayList<T> fetchList(Class<T> cls, String... keys) {
        if (!this.hasData())
            throw Errors.exception(DataSourceException.class, "ds:no-data", this.getName());

        ISig sig = SigUtils.buildCompSig(cls, keys).apply(new SigClassUnifier(cls));
        ISig lsig = new ListSig(sig);
        ISchema sch = this.dataAccess.getSchema();
        IDataOp<Stream<T>> op = SchemaSigUnifier.unifyWith(sch, lsig);
        Stream<T> d = op.apply(this.dataAccess);
        return d.collect(Collectors.toCollection(ArrayList::new));
    }

    /* The rest of the fetch...() methods are derived from the above */

    public <T> T fetch(String clsName, String... keys) {
        return fetch(SigUtils.classFor(clsName), keys);
    }
    
    @SuppressWarnings("unchecked")
    public <T> ArrayList<T> fetchList(String clsName, String... keys) {
        return (ArrayList<T>) fetchList(SigUtils.classFor(clsName), keys);
    }

    @SuppressWarnings("unchecked")
    public <T> T[] fetchArray(String clsName, String... keys) {
        return (T[]) fetchArray(SigUtils.classFor(clsName), keys);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T[] fetchArray(Class<T> cls, String... keys) {
        T[] ts = (T[]) Array.newInstance(cls, 1);
        return (T[]) (fetchList(cls, keys).toArray(ts));
    }
    
    public boolean fetchBoolean(String key) { return fetch(Boolean.class, key); }
    public byte fetchByte(String key) { return fetch(Byte.class, key); }
    public char fetchChar(String key) { return fetch(Character.class, key); }
    public double fetchDouble(String key) { return fetch(Double.class, key); }
    public float fetchFloat(String key) { return fetch(Float.class, key); }
    public int fetchInt(String key) { return fetch(Integer.class, key); }
    public String fetchString(String key) { return fetch(String.class, key); }
    
    public boolean[] fetchBooleanArray(String key) {
        return ArrayUtils.toPrimitive(fetchArray(Boolean.class, key));
    }
    public byte[] fetchByteArray(String key) {
        return ArrayUtils.toPrimitive(fetchArray(Byte.class, key));
    }
    public char[] fetchCharArray(String key) {
        return ArrayUtils.toPrimitive(fetchArray(Character.class, key));
    }
    public double[] fetchDoubleArray(String key) {
        return ArrayUtils.toPrimitive(fetchArray(Double.class, key));
    }
    public float[] fetchFloatArray(String key) {
        return ArrayUtils.toPrimitive(fetchArray(Float.class, key));
    }
    public int[] fetchIntArray(String key) {
        return ArrayUtils.toPrimitive(fetchArray(Integer.class, key));
    }
    public String[] fetchStringArray(String key) {
        return fetchArray(String.class, key);
    }
    
    public ArrayList<Boolean> fetchBooleanList(String key) {
        return fetchList(Boolean.class, key);
    }
    public ArrayList<Byte> fetchByteList(String key) {
        return fetchList(Byte.class, key);
    }
    public ArrayList<Character> fetchCharList(String key) {
        return fetchList(Character.class, key);
    }
    public ArrayList<Double> fetchDoubleList(String key) {
        return fetchList(Double.class, key);
    }
    public ArrayList<Float> fetchFloatList(String key) {
        return fetchList(Float.class, key);
    }
    public ArrayList<Integer> fetchIntList(String key) {
        return fetchList(Integer.class, key);
    }
    public ArrayList<String> fetchStringList(String key) {
        return fetchList(String.class, key);
    }



}
