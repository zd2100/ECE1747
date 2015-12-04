package dbCache.contract;

import dbCache.models.DataSet;

public interface IDataProvider {
	public DataSet executeQuery(String query);
}
