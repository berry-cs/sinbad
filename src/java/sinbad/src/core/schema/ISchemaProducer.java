package core.schema;

/**
 * An object that produces a schema for a particular
 * data source. 
 */
public interface ISchemaProducer {
    ISchema getSchema();
}
