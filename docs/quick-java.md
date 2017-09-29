
# Quick Reference (Java)

**Contents**

* [Basic Template](#basic-template)
    + [Template for Processing](#template-for-processing)
* [Display Summary of Available Data](#display-summary-of-available-data)
* [Other Connection Methods](#other-connection-methods)
* [Connection Parameters](#connection-parameters)
* [Data Source Options](#data-source-options)
* [Selecting From a .zip Archive](#selecting-from-a-zip-archive)
* [Cache Control](#cache-control)
* [Disable Download Progress Display](#disable-download-progress-display)
* [View Preferences](#view-preferences)
* [Using an Iterator](#using-an-iterator)
* [Fetching Data](#fetching-data)


## Basic Template

    import core.data.*
    ...
    DataSource ds = DataSource.connect("<URL>");
    ds.load();
    ...
    ds.fetch...("<field>");  // see fetch...() methods below

### Template for Processing

    import core.data.*;
    ...
    void setup() {
        DataSource.initializeProcessing(this);
        ...
    }
Then use `connect`, `load`, and `fetch...` as above.


## Display Summary of Available Data
After `ds.load()`:

    ds.printUsageString() 


## Other Connection Methods
Specify a data format (`"CSV"`, `"XML"`, `"JSON"`, etc.):

    DataSource ds = DataSource.connectAs("<FORMAT>", "<URL>");

Connect using a data specification file (e.g. provided by instructor):

    DataSource ds = DataSource.connectUsing("<URL>");


## Connection Parameters
Some data sources may require additional _parameters_ to construct
the URL. Use `setParam("<name>", "<value>")` after the `connect` and 
before `load`. For example:

    ds.setParam("format", "raw");


## Data Source Options
Some data sources provide (or require) additional information to
process them once they have been downloaded. The available _options_
are format-specific and are listed by enabling verbose usage info:

    ds.printUsageString(true);

Use 

    ds.setOption("<name>", "<value>");

For example (with a CSV data source):

    ds.setOption("header", "ID,Name,Call sign,Country,Active");


## Selecting From a .zip Archive
To use a file that is one of several in a ZIP archive, set
the "file-entry" option:

    ds.setOption("file-entry", "FACTDATA_MAR2016.TXT");


## Cache Control
Control frequency of caching (or disable it):

    ds.setCacheTimeout(<minutes>); 
    // may use  CacheConstants.NEVER_CACHE
    //      or  CacheConstants.NEVER_RELOAD (always caches)

Show where files are cached:

    System.out.println(ds.getCacheDirectory());
    
Clear all cache files (for *all* data sources):

    ds.clearENTIRECache();


## Disable Download Progress Display
To turn off the dots that are printed while files are being downloaded:

    DataSource.showDownloadProgress(boolean)

Note, this is a global setting and will apply to all data sources that
are loaded after this statement has been executed.


## View Preferences
Launch preferences GUI window.

    DataSource.preferences();
    
When preferences are saved, the program will immediately terminate and exit. Comment out or delete the statement above to enable the program to continue running as usual.


## Using an Iterator

    DataSourceIterator iter = ds.iterator();
    while (iter.hasData()) {
        String name = iter.fetchString("Name");
        boolean active = iter.fetchBoolean("Active");
        System.out.println(name + ": " + active);
        iter.loadNext();
    }


## Fetching Data
Extract data by field names/paths using the appropriate method(s):

    // PRIMITIVE TYPE VALUES
    public boolean fetchBoolean(String key);
    public byte fetchByte(String key); 
    public char fetchChar(String key);
    public double fetchDouble(String key);
    public float fetchFloat(String key);
    public int fetchInt(String key);
    public String fetchString(String key);
    	
    // ARRAYS
    public boolean[] fetchBooleanArray(String key);
    public byte[] fetchByteArray(String key);
    public char[] fetchCharArray(String key);
    public double[] fetchDoubleArray(String key);
    public float[] fetchFloatArray(String key);
    public int[] fetchIntArray(String key);
    public String[] fetchStringArray(String key);
    	
    // LISTS
    public ArrayList<Boolean> fetchBooleanList(String key);
    public ArrayList<Byte> fetchByteList(String key);
    public ArrayList<Character> fetchCharList(String key);
    public ArrayList<Double> fetchDoubleList(String key);
    public ArrayList<Float> fetchFloatList(String key);
    public ArrayList<Integer> fetchIntList(String key);
    public ArrayList<String> fetchStringList(String key);
    
    // OBJECTS (of any class you name - the order of key names
    //          should match a constructor of the class)
    public <T> T fetch(String clsName, String... keys);
    public <T> ArrayList<T> fetchList(String clsName, String... keys);
    public <T> T[] fetchArray(String clsName, String... keys);


