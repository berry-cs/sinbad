package core.access;

import java.util.stream.Stream;

import core.schema.ISchema;

import static core.log.Errors.*;

/**
 * A data access object that fails for every operation. Mostly intended as
 * a base class to be extended and desired methods overridden.
 *
 */
public class FailAccess implements IDataAccess {
    public String getContents() {
        throw exception(DataAccessException.class, "da:getcontents");
    }

    public IDataAccess get(String path, int i) {
        throw exception(DataAccessException.class, "da:get-index", i, path);
    }

    public IDataAccess get(String path) {
        throw exception(DataAccessException.class, "da:get-path", path);
    }
    
    public Stream<IDataAccess> getAll(String path) {
        throw exception(DataAccessException.class, "da:get-list", path);
    }

    public ISchema getSchema() {
        throw exception(DataAccessException.class, "da:schema");
    }
}
