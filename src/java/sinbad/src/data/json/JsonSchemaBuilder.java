package data.json;

import static core.log.Errors.exception;

import org.apache.commons.lang3.ClassUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import core.access.DataAccessException;
import core.schema.*;

public class JsonSchemaBuilder {
    
    // could be a Boolean, Double, Integer, Long, String, JSONArray, JSONObject, or null
    public static ISchema inferSchema(Object data) {
        return inferSchema(data, null);
    }
    
    // path could be null, or a field selector (coming from inferCompSchema)
    public static ISchema inferSchema(Object data, String path) {
        if (isNull(data)) {
            throw exception(DataAccessException.class, "da:schema");
        } else if (isPrimitiveOrWrapperOrJsonNull(data.getClass())) {
            return new PrimSchema(path);
        } else if (data instanceof JSONArray) {
            return inferArraySchema( (JSONArray) data, path );
        } else if (data instanceof JSONObject) {
            return inferCompSchema( (JSONObject) data, path );
        } else {
            System.err.println("Problem: " + data);
            throw exception(DataAccessException.class, "da:schema");
        }
    }

    private static ISchema inferCompSchema(JSONObject data, String path) {
        String[] fieldNames = JSONObject.getNames(data);
        CompField[] flds = new CompField[fieldNames.length];
        int i = 0;
        for (String field : fieldNames) {
            flds[i++] = new CompField(field, inferSchema(data.get(field), field)); // this is where path gets passed down
        }
        CompSchema cs = new CompSchema(path, flds);
        return cs;
    }

    private static ISchema inferArraySchema(JSONArray data, String path) {
        final int N = data.length();
        if (N == 0) {
            ListSchema ls = new ListSchema(path, new PrimSchema());
            return ls;
        }
        
        // first see if everything is the same type of thing
        boolean allSame = true;
        Object first = data.get(0);
        for (int i = 1; i < N; i++) {
            if (!sameJSONType(first, data.get(i)))
                allSame = false;                    
        }
        
        if (allSame) {
            ListSchema ls = new ListSchema(path, inferSchema(first));
            return ls;
        } else {
            CompField[] flds = new CompField[N];
            for (int i = 0; i < N; i++) {
                // use index as field name
                flds[i] = new CompField(""+i, inferSchema(data.get(i)));
            }
            CompSchema cs = new CompSchema(path, flds);
            return cs;
        }
    }
    
    private static boolean sameJSONType(Object a, Object b) {
        if ((isNull(a) && !isNull(b)) || (!isNull(a) && isNull(b))) {
            return false;
        }
        return (   (isNull(a) && isNull(b))
                || (isPrimitiveOrWrapperOrJsonNull(a.getClass()) && isPrimitiveOrWrapperOrJsonNull(b.getClass()))
                || (a instanceof JSONArray && b instanceof JSONArray)   // TODO: this needs to continue
                || (a instanceof JSONObject && b instanceof JSONObject) ); // TODO: need to refine
    }

    private static <T> boolean isPrimitiveOrWrapperOrJsonNull(Class<T> klass) {
        return String.class == klass || ClassUtils.isPrimitiveOrWrapper(klass)
                || JSONObject.NULL.getClass() == klass;
    }    
    
    private static boolean isNull(Object o) {
        return o == null;
    }


}
