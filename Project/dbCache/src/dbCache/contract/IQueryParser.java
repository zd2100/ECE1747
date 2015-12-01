package dbCache.contract;

import dbCache.models.Request;

public interface IQueryParser {
	public void parseQuery(Request request);
}
