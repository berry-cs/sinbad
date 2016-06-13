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
        
        addError("ds:notype", "no data source plugin for type %s", 1);
        addError("ds:noinfer", "could not infer the type of data at %s", 1);
        addError("ds:notready-params", "not ready to load; missing parameters: %s", 1);
        addError("ds:no-input", "failed to load data source; no input stream: %s", 1);
        addError("ds:no-data", "No data available: %s --- make sure you called .load()", 1);
        addError("ds:no-class", "Could not find a class named: %s", 1);
        
        addError("da:no-such-option", "no such option: %s", 1);
        addError("da:getcontents", "could not access contents as a primitive (string)", 0);
        addError("da:get-index", "could not access index %d of path %s", 2);
        addError("da:get-path", "could not access path %s", 1);
        addError("da:get-list", "could not access %s (not a list?)", 1);
        addError("da:schema", "failed to build schema", 0);
        addError("da:construct", "failed to initialize data: %s", 1);

        addError("em:no-exception", "error instantiating exception class: %s", 1);
        addError("em:no-tag", "error message tag not found: %s", 1);
        addError("em:wrong-count", "incorrect argument count for error message: %s", 1);
        
        addError("scunify:unknown-sig", "unexpected/unhandled signature type: %s", 1);
        addError("scunify:not-prim", "%s is not a primitive class", 1);
        addError("scunify:prim-mismatch", "%s cannot be unified with %s", 2); //  sig first, then the java class type   
        addError("scunify:unify-fail", "cannot unify %s with %s", 2);
        addError("scunify:unify-fail/bc", "cannot unify %s with %s because %s", 3);
        addError("scunify:comp-classmismatch", "class of %s does not match %s" , 2); // sig first
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
