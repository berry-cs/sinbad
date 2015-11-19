package core.data;

/**
 * Interface for iterators over data sources
 * 
 * @author Nadeem Abdul Hamid
 *
 */
public interface DataSourceIterator {
	public boolean hasData();
	public DataSourceIterator loadNext();
	
	public <T> T fetch(String clsName, String... keys);
	public <T> T fetch(Class<T> cls, String... keys);

	public boolean fetchBoolean(String key);
	public byte fetchByte(String key);
	public char fetchChar(String key);
	public double fetchDouble(String key);
	public float fetchFloat(String key);
	public int fetchInt(String key);
	public String fetchString(String key);
	
	public String usageString();
}
