package core.schema;

import java.io.Serializable;

public interface ISchema extends Serializable {
    /** 
     * The base path of a schema object represents a path (possibly null) in
     * the underlying data source that needs to be traversed in order to get
     * to the element whose structure is represented by this schema object.
     *  
     * @return the base path of the data element (can be null)
     */
    public String getPath();
    
    /**
     * @return a human-friendly description of the data element (can be null)
     */
    public String getDescription();
    
    public <T> T apply(ISchemaVisitor<T> sv);

    public String toString(boolean verbose);
}
