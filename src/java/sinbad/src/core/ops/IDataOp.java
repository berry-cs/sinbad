package core.ops;

import core.access.IDataAccess;

interface IDataOp<T> {
	T apply(IDataAccess d);
}