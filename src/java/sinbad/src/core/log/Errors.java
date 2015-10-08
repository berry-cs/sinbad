package core.log;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * 
 * Provides exception generating/reporting facilities in the form of a database
 * of exception messages and static methods for creating exceptions and
 * formatting error messages
 *  
 */
public class Errors {
    private static HashMap<String, ErrorMessage> errdb;
    
    static {  // static initialization block
        errdb = new HashMap<String, ErrorMessage>(); 
        
        addError("da:getcontents", "could not access contents as a primitive (string)", 0);
        addError("da:get-index", "could not access index %d of path %s", 2);
        addError("da:get-path", "could not access path %s", 1);
        addError("da:get-list", "could not access %s (not a list?)", 1);

        addError("em:no-exception", "error instantiating exception class: %s", 1);
        addError("em:no-tag", "error message tag not found: %s", 1);
        addError("em:wrong-count", "incorrect argument count for error message: %s", 1);
        
        addError("scunify:unknown-sig", "unexpected/unhandled signature type", 0);
        addError("scunify:not-prim", "%s is not a primitive class", 1);
    }
    
    public static RuntimeException exception(Class<? extends RuntimeException> exnClass, String tag, Object ... args) {
        try {
            return exnClass.getConstructor(String.class).newInstance(Errors.generate(tag, args));
        } catch (InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(generate("em:no-exception", exnClass.getName()));
        }
    }
    
    public static String generate(String tag, Object ... args) {
        ErrorMessage em = errdb.get(tag);
        if (em == null) {
            throw exception(RuntimeException.class, "em:no-tag", tag);
        } else {
            return em.generate(args);
        }
    }
    
    private Errors() {}

    private static void addError(String tag, String descrip, int argCount) {
        errdb.put(tag, new ErrorMessage(tag, descrip, argCount));        
    }
    
}
