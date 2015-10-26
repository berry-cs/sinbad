package core.access.raw;

import core.access.IDataAccess;
import core.schema.ISchema;
import core.schema.ISchemaProducer;

public interface IRawAccess extends IDataAccess, ISchemaProducer {
    ISchema getSchema(String basePath);
}
