package core.schema;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Represents a structured data element whose subelements
 * can be accessed by 'field' names (which may be distinct from
 * the actual base path of those subelements in the underlying data)
 */
public class CompSchema extends AbsSchema {
    private String[] fieldNames;
    private HashMap<String, ISchema> fieldMap;
    
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
}
