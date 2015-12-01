package dbCache.contract;

import java.sql.ResultSet;

public interface IDataProvider {
	public ResultSet executeQuery(String query);
}
