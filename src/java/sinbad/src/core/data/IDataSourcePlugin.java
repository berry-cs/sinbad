package core.data;

import core.access.IDataAccessFactory;
import core.infer.IDataFormatInfer;

public interface IDataSourcePlugin {
    IDataFormatInfer getInfer();
    IDataAccessFactory getFactory();
}
