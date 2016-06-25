
Sinbad (Java version)
# Quick Start

## Basic Template

    import core.data.*
    ...
    DataSource ds = DataSource.connect("<URL>");
    ds.load();
    ...
    ds.fetchXYZ("<field>");

## Display Summary of Available Data
After `ds.load()`:

   ds.printUsageString() 

## Other Connection Options
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
the "fileentry" option:

   ds.setOption("fileentry", "FACTDATA_MAR2016.TXT");



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


