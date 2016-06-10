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
        if (isNull(data)) {
            throw exception(DataAccessException.class, "da:schema");
        } else if (isPrimitiveOrWrapperOrJsonNull(data.getClass())) {
            return new PrimSchema();
        } else if (data instanceof JSONArray) {
            return inferArraySchema( (JSONArray) data );
        } else if (data instanceof JSONObject) {
            return inferCompSchema( (JSONObject) data );
        } else {
            System.err.println("Problem: " + data);
            throw exception(DataAccessException.class, "da:schema");
        }
    }

    private static ISchema inferCompSchema(JSONObject data) {
        String[] fieldNames = JSONObject.getNames(data);
        CompField[] flds = new CompField[fieldNames.length];
        int i = 0;
        for (String field : fieldNames) {
            flds[i++] = new CompField(field, inferSchema(data.get(field)));
        }
        CompSchema cs = new CompSchema(flds);
        return cs;
    }

    private static ISchema inferArraySchema(JSONArray data) {
        final int N = data.length();
        // first see if everything is the same type of thing
        boolean allSame = true;
        Object first = data.get(0);
        for (int i = 1; i < N; i++) {
            if (!sameJSONType(first, data.get(i)))
                allSame = false;                    
        }
        
        if (allSame) {
            ListSchema ls = new ListSchema(inferSchema(first));
            return ls;
        } else {
            CompField[] flds = new CompField[N];
            for (int i = 0; i < N; i++) {
                // use index as field name
                flds[i] = new CompField(""+i, inferSchema(data.get(i)));
            }
            CompSchema cs = new CompSchema(flds);
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
