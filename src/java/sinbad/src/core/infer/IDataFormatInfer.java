package core.infer;

import java.util.Map;

/**
 * An interface for objects that attempt to determine if 
 * a particular data source is of some specific format.
 */
public interface IDataFormatInfer {
    /**
     * 
     * @param path the primary path (URL/file name) to the data
     * @return
     */
    boolean matchedBy(String path);
    
    /**
     * Produces a map of inferred options that should be passed to the data
     * access factory.
     * <strong>NOTE</strong>: this method only makes sense, if ever, 
     * *after* <code>matchedBy</code> has been invoked.
     */
    Map<String,String> getOptions();
}
