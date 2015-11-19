package core.schema;

public class ListSchema extends AbsSchema {
    private static final long serialVersionUID = 1L;

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((elementSchema == null) ? 0 : elementSchema.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (!(obj instanceof ListSchema))
            return false;
        ListSchema other = (ListSchema) obj;
        if (elementSchema == null) {
            if (other.elementSchema != null)
                return false;
        } else if (!elementSchema.equals(other.elementSchema))
            return false;
        return true;
    }


}
