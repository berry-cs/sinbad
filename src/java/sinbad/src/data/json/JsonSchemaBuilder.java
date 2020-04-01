package data.json;

import static core.log.Errors.exception;

import java.util.HashMap;

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

    private static ISchema mergeSchemas(ISchema sch, Object data) {
        if (sch instanceof CompSchema && data instanceof JSONObject) {
            return mergeCompSchemas((CompSchema) sch, (JSONObject) data);
        } else if (sch instanceof ListSchema && data instanceof JSONArray) {
            return mergeListSchemas((ListSchema) sch, (JSONArray) data);
        } else {
            return sch;
        }
    }
    

    private static ISchema inferCompSchema(JSONObject data, String path) {
        String[] fieldNames = JSONObject.getNames(data);  // produced null once for `https://www.loc.gov/collections/?fo=json`
        CompField[] flds = new CompField[0];
        
        if (fieldNames != null) {
            flds = new CompField[fieldNames.length];
            int i = 0;
            for (String field : fieldNames) {
                flds[i++] = new CompField(field, inferSchema(data.get(field), field)); // this is where path gets passed down
            }
        }
        CompSchema cs = new CompSchema(path, flds);
        return cs;
    }
    
    
    private static ListSchema mergeListSchemas(ListSchema sch, JSONArray data) {
        ISchema eltSchema = sch.getElementSchema();
        for (Object dataElt : data) {
            eltSchema = mergeSchemas(eltSchema, dataElt);
        }
        return new ListSchema(sch.getPath(), sch.getDescription(), eltSchema);
    }
    
    // this adds additional fields to an existing compound schema, for cases of lists of dictionaries, where not
    // every element in the list has the same fields
    private static CompSchema mergeCompSchemas(CompSchema sch, JSONObject data) {
        HashMap<String, ISchema> oldFlds = sch.getFieldMap();
        String[] fieldNames = JSONObject.getNames(data);
        if (fieldNames != null) {
            int countNew = 0;
            for (String fn : fieldNames) { if (!oldFlds.containsKey(fn)) countNew++; }
            CompField[] newFields = new CompField[oldFlds.size() + countNew];
            int cur = 0;
            for (String fn : oldFlds.keySet()) {  // copy over the old fields, but merge them as we go too...
                ISchema oldSchema = oldFlds.get(fn);
                if (data.has(fn)) {
                    Object dataOffn = data.get(fn);
                    newFields[cur++] = new CompField(fn, mergeSchemas(oldSchema, dataOffn));
                } else {
                    newFields[cur++] = new CompField(fn, oldSchema);
                }
            }
            for (String fn : fieldNames) {
                if (!oldFlds.containsKey(fn)) {
                    newFields[cur++] = new CompField(fn, inferSchema(data.get(fn), fn));
                }
            }
            sch = new CompSchema(sch.getPath(), sch.getDescription(), newFields);
        }        
        return sch;
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
            ISchema eltSchema = inferSchema(first);
            ISchema ls = mergeSchemas(new ListSchema(path, eltSchema), data);
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
