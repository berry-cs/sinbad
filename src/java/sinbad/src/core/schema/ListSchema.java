package core.schema;

public class ListSchema extends AbsSchema {
    private ISchema elementSchema;

    public ListSchema(ISchema elementSchema) {
        this(null, null, elementSchema);            
    }
    
    public ListSchema(String basePath, ISchema elementSchema) {
        this(basePath, null, elementSchema);            
    }
    
    public ListSchema(String basePath, String description, ISchema elementSchema) {
        super(basePath, description);
        this.elementSchema = elementSchema;                
    }

    public ISchema getElementSchema() {
        return elementSchema;
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
        String m = String.format("[%s]", this.elementSchema.toString(verbose));

        if (verbose && this.path != null) {
            return pathPrefix + m;
        } else {
            return m;
        }
    }


}
