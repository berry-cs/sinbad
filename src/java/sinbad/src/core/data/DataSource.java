package core.data;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import data.json.JsonFactory;
import data.json.JsonInfer;
import data.xml.XmlInfer;
import data.xml.XmlFactory;

public class DataSource implements IDataSource {

    /* =========================================================
     * STATIC MEMBERS
     */

    // ALL KEYS SHOULD BE UPPERCASE STRINGS
    private static HashMap<String,IDataSourcePlugin> plugins = new HashMap<String,IDataSourcePlugin>();
    
    static {
        plugins.put("CSV", new IDataSourcePlugin() {
            public IDataFormatInfer getInfer() { return new CsvInfer(); }
            public IDataAccessFactory getFactory() { return new CsvFactory(); } });
        plugins.put("TSV", new IDataSourcePlugin() {
            public IDataFormatInfer getInfer() { return new CsvInfer("\t"); }
            public IDataAccessFactory getFactory() { return new CsvFactory(); } });
        plugins.put("XML", new IDataSourcePlugin() {
            public IDataFormatInfer getInfer() { return new XmlInfer(); }
            public IDataAccessFactory getFactory() { return new XmlFactory(); } });
        plugins.put("JSON", new IDataSourcePlugin() {
            public IDataFormatInfer getInfer() { return new JsonInfer(); }
            public IDataAccessFactory getFactory() { return new JsonFactory(); } });
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

        // TODO: DataSourceLoader.isValidDataSourceSpec(path)
        for (String typeExt : plugins.keySet()) {
            IDataSourcePlugin dsp = plugins.get(typeExt);
            if (dsp.getInfer().matchedBy(path)) {
                return connect(path, typeExt, dsp);
            } 
        }
        
        throw Errors.exception(DataSourceException.class, "ds:noinfer", path);
    }
    
    /** 
     * Use the specified tag (type extension) to determine a data source
     * factory to use to load the given path.
     * @param path file path or URL to data
     * @param typeExt a standard type extension (e.g. "CSV", "XML", "JSON")
     * @return a prepared data source
     */
    public static DataSource connectAs(String typeExt, String path) {
        typeExt = typeExt.toUpperCase();
        if (!plugins.containsKey(typeExt)) {
            throw Errors.exception(DataSourceException.class, "ds:notype", typeExt);
        } 
        return connect(path, typeExt, plugins.get(typeExt));
    }
    
    public static DataSource connect(String path, String typeExt, IDataSourcePlugin plugin) {
        return new DataSource(path, path, typeExt, plugin);
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
    protected String formatType;  // should correspond to the keys in the <code>plugins</code> map
    
    /* params are connection-related parameters, either in a query string or filling in part of the path */
    protected HashMap<String, Param> params; // keeps track of *all* parameters available for this data source
    protected ArrayList<String> paramValueKeys; // keeps track of which parameters have been supplied so far
    protected HashMap<String, String> paramValues; // keeps track of the values of the supplied parameters

    protected boolean readyToLoad;
    protected boolean loaded;

    protected IDataFormatInfer dataInfer;
    protected IDataAccessFactory dataFactory;
    protected IDataAccess dataAccess;
    protected DataCacher cacher;
    
    protected DataSource(String name, String path, String typeExt, IDataSourcePlugin plugin) {
        this.name = name;
        this.path = path;
        this.description = null;
        this.infoURL = null;
        this.formatType = typeExt;
        
        this.params = new HashMap<String, Param>();
        this.paramValues = new HashMap<String, String>();
        this.paramValueKeys = new ArrayList<String>();

        this.readyToLoad = false;
        this.loaded = false;

        this.dataInfer = plugin.getInfer();
        this.dataFactory = plugin.getFactory();
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
        if (verbose && formatType != null) s += "Format: " + formatType + "\n";
        
        String[] paramKeys = params.keySet().toArray(new String[]{});
        if (paramKeys.length > 0) {
            Arrays.sort(paramKeys);
            s += "\nThe following (connection) parameters may/must be set on this data source:\n";
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
        
        String[] optionKeys = this.dataFactory.getOptions();
        if (verbose && optionKeys.length > 0) {
            Arrays.sort(optionKeys);
            s += "\nThe following options are available for this data source format:\n";
            for (String key : optionKeys) {
                s += "   - " + key + "\n";
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
        this.dataFactory.setSchema(schema);
        return this;
    }

    public IDataAccess getDataAccess() {
        return this.dataAccess;
    }
    
    /**
     * Sets the cache timeout value, in <em>minutes</em>.
     */
    public DataSource setCacheTimeout(int val) {
        this.cacher = this.cacher.updateTimeout((long)val * 1000L * 60L);
        return this;
    }

    @Override
    public DataSource setCacheDirectory(String path) {
        this.cacher = this.cacher.updateDirectory(path);
        return this;
    }
    
    public String getCacheDirectory() {
        return this.cacher.getDirectory();
    }
    
    public long getCacheTimeout() {
        return this.cacher.getTimeout();
    }
    
    public boolean clearENTIRECache() {
        return this.cacher.clearCache();
    }
    
    
    /*
     * HANDLING PARAMETERS
     */
    
    public DataSource addParam(Param param) {
        if (param != null) 
            params.put(param.getKey(), param);
        return this;
    }

    public DataSource setParam(String op, String value) {
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
        this.dataFactory.setOption(op, value);
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
        if (!readyToLoad()) {
            throw Errors.exception(DataSourceException.class, "ds:notready-params",
                    StringUtils.join(missingParams().toArray(new String[]{}), ','));
        }
        
        if (this.loaded && !forceReload) {
            return this;
        }
    
        // get the raw data into/from the cache
        String resolvedPath = this.cacher.resolvePath(this.getFullPathURL());
        if (resolvedPath == null) 
            throw Errors.exception(DataSourceException.class, "ds:no-input", path);
        
        // load any inferred options and add them to the factory
        this.dataInfer.matchedBy(path); // because getOptions is only valid after matchedBy has been invoked
        Map<String,String> options = this.dataInfer.getOptions();
        for (Entry<String,String> e : options.entrySet()) { 
            this.dataFactory.setOption(e.getKey(), e.getValue());
        }
        
        // load schema from cached if appropriate and add it to the factory...
        boolean cachedSchemaLoaded = false;
        String cachedSchemaPath = this.cacher.resolvePath(this.getFullPathURL(), "schema");
        if (!this.dataFactory.hasSchema() && cachedSchemaPath != null && !forceReload) {
            try {
                InputStream cis = IOUtil.createInput(cachedSchemaPath);
                //if (cis != null) {
                    ObjectInputStream schis = new ObjectInputStream(cis);
                    ISchema schema =  (ISchema)schis.readObject();
                    schis.close();
                    this.dataFactory.setSchema(schema);  // ... here
                    //System.out.println("loaded cached schema: " + cachedSchemaPath);
                    cachedSchemaLoaded = true;
                //}
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        
        // now use the factory to generate the data access object
        InputStream is = IOUtil.createInput(resolvedPath); 
        this.dataAccess = this.dataFactory.newInstance(is); 
        ISchema schema = this.dataAccess.getSchema();  // forces it to be built
        
        // cache the schema for later if it wasn't already
        if (!cachedSchemaLoaded) {
            try {
                PipedInputStream pipis = new PipedInputStream();
                PipedOutputStream pipos = new PipedOutputStream(pipis);
                
                Thread thr =
                        new Thread() {
                            public void run() {
                                try {
                                    ObjectOutputStream schos = new ObjectOutputStream(pipos);
                                    schos.writeObject(schema);
                                    schos.close();
                                } catch (IOException e) {
                                    DataSource.this.cacher.clearCacheData(DataSource.this.getFullPathURL(), 
                                                                          "schema");
                                }
                            }
                        };
                thr.start();
                
                this.cacher.addToCache(this.getFullPathURL(), "schema", pipis);
                pipis.close();
                
                thr.join();
                pipos.close();
            } catch (IOException | InterruptedException e) {
                // oh well, didn't work, so just clear it out of the cache completely
                // in case it was partially stored or something
                this.cacher.clearCacheData(this.getFullPathURL(), "schema");
            }
        } 

        this.loaded = true;
        
        return this;
    }
    
    public String getFullPathURL() {
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
        
        ISig presig = SigUtils.buildCompSig(cls, keys);
        ISig sig = presig.apply(new SigClassUnifier(cls));
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

    public DataSourceIterator iterator() {
        return new GenDataSourceIterator(this);
    }

    public long size() {
        if (!hasData()) 
            return 0;
        else
            return (long) this.dataAccess.getAll(this.dataAccess.getSchema().getPath()).count();
    }

}
