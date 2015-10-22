package core.ops;

import core.access.IDataAccess;

/**
 * A data op is an expression that can be applied to a IDataAccess to manipulate the data.
 * @author 
 *
 * @param <T> The type of the result of the expression
 */
public interface IDataOp<T> {
	T apply(IDataAccess d);
}