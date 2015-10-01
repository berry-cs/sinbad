package core.schema;


/**
 * Represents a data element that contains unstructured, "raw" data
 */
public class PrimSchema extends AbsSchema {
    
    /**
     * Construct a primitive schema with null base path and description
     */
    public PrimSchema() {
        super();
    }

    /**
     * @param basePath
     * @param description
     */
    public PrimSchema(String basePath, String description) {
        super(basePath, description);
    }

    /**
     * @param basePath
     */
    public PrimSchema(String basePath) {
        super(basePath);
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
        if (verbose && this.path != null) {
            return String.format("(%s)_*", this.path);
        } else {
            return "*"; 
        }
    }
}

