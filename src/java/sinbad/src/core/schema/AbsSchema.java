package core.schema;

public abstract class AbsSchema implements ISchema {
    //private static boolean verboseToString = false;
    
    protected String path;          // can be null
    protected String description;   // can be null
    
    /**
     * Constructs a schema with null description and basePath.
     */
    public AbsSchema() {
        this(null, null);
    }

    /**
     * Constructs a schema with null description.
     * @param basePath path to this element in the data (can be null)
     */
    public AbsSchema(String basePath) {
        this(basePath, null);
    }
    
    /**
     * @param basePath path to this element in the data (can be null)
     * @param description a short description (can be null)
     */
    public AbsSchema(String basePath, String description) {
        this.path = basePath;
        this.description = description;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public <T> T apply(ISchemaVisitor<T> sv) {
        return sv.defaultVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof AbsSchema))
            return false;
        AbsSchema other = (AbsSchema) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        return true;
    }

    /*
    public static boolean verboseToString() {
        return verboseToString;
    }

    public static void setVerboseToString(boolean verboseToString) {
        AbsSchema.verboseToString = verboseToString;
    }
    */
    
}
