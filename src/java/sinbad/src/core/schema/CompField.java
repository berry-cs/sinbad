package core.schema;

/**
 * Represents the field name + schema pairs of a compound schema (CompSchema) 
 * object.
 */
public class CompField {
    String name;
    ISchema schema;

    public CompField(String name, ISchema schema) {
        this.name = name;
        this.schema = schema;
    }

    public String getName() {
        return name;
    }

    public ISchema getSchema() {
        return schema;
    }  
}
