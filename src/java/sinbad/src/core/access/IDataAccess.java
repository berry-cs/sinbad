package core.access;

import java.util.stream.Stream;

import core.schema.ISchemaProducer;

/**
 * 
 * Provides a generic way to traverse data obtained from
 * an arbitrary data source. Interpretation of paths is
 * data source-specific. A DataAccess object is like a cursor
 * into the data, allowing movement through it (in one 
 * direction), and at every moment in time encapsulating a 
 * particular element in the data.
 * 
 * Path values could potentially be <pre>null</pre>. Some
 * types of data access object may ignore certain path
 * variables. 
 * 
 */
public interface IDataAccess extends ISchemaProducer {
      
    /** 
     * Access the contents of the current data element
     * 
     * @return the entire contents of this data element
     */
    String getContents();
    
    /**
     * Access the i'th data element that matches path
     * 
     * @param path the path to a collection of data elements 
     * @param i index of the element to access
     * @return a data access object for the specified element
     */
    IDataAccess get(String path, int i);
    
    /**
     * Access a data element that matches path
     * 
     * @param path the path to a data element
     * @return a data access object for the specified element
     */
    IDataAccess get(String path);
    
    /**
     * Access a stream of data elements that match path
     * 
     * @param path the path to a collection of data elements 
     * @return a stream of data access objects for the specified elements
     */
    Stream<IDataAccess> getAll(String path);
}
