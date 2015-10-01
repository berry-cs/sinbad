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

    /*
    public static boolean verboseToString() {
        return verboseToString;
    }

    public static void setVerboseToString(boolean verboseToString) {
        AbsSchema.verboseToString = verboseToString;
    }
    */
    
}
