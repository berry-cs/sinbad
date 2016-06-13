package core.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a structured data element whose subelements
 * can be accessed by 'field' names (which may be distinct from
 * the actual base path of those subelements in the underlying data)
 */
public class CompSchema extends AbsSchema {
    private static final long serialVersionUID = 1L;
    
    private String[] fieldNames;   // this is kept to maintain the order of the fields
    private HashMap<String, ISchema> fieldMap; // schema of each field
    
    /**
     * Construct a compound schema with null base path and description
     */
    public CompSchema(CompField ... flds) {
        this(null, null, flds);
    }

    public CompSchema(String basePath, String description, CompField ... flds) {
        super(basePath, description);
        fieldMap = new HashMap<String, ISchema>();
        fieldNames = Arrays.asList(flds).stream()
                .map(cf -> { fieldMap.put(cf.name, cf.schema);    // adds to the hashmap
                             return cf.getName(); })
                .toArray(n -> new String[n]);
    }

    public CompSchema(String basePath, CompField ... flds) {
        this(basePath, null, flds);
    }

    @Override
    public <T> T apply(ISchemaVisitor<T> sv) {
        return sv.visit(this);
    }

    @Override
    public String toString() {
        return toString(false);
    }
    
    public String toString(boolean verbose) {
        String pathPrefix = String.format("(%s)_", this.path);
        
        String m = "{";
        boolean firstDone = false;
        for (String k : this.fieldNames) {
            if (firstDone) { m += ", "; } else { firstDone = true; }
            m += (k + ": " + fieldMap.get(k).toString(verbose));
        }
        m += "}";
        
        if (verbose && this.path != null) {
            return pathPrefix + m;
        } else {
            return m;
        }
    }

    public HashMap<String, ISchema> getFieldMap() {
        return this.fieldMap;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((fieldMap == null) ? 0 : fieldMap.hashCode());
        result = prime * result + Arrays.hashCode(fieldNames);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof CompSchema))
            return false;
        CompSchema other = (CompSchema) obj;
        if (fieldMap == null) {
            if (other.fieldMap != null)
                return false;
        } else if (!fieldMap.equals(other.fieldMap))
            return false;
        if (!Arrays.equals(fieldNames, other.fieldNames))
            return false;
        return true;
    }
    
    public Map<String, Object> export() {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "struct");
        this.exportCommon(m);

        List<Object> fields = new ArrayList<Object>();
        for (String field : this.fieldNames) {
            Map<String, Object> fm = new HashMap<String, Object>();
            fm.put("name", field);
            fm.put("schema", fieldMap.get(field).export());
            fields.add(fm);
        }
        m.put("fields", fields);
        
        return m;
    }
}
