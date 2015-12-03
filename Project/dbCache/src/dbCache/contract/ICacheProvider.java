package dbCache.contract;

import dbCache.models.Request;

public interface ICacheProvider {
	public boolean executeQuery(Request request);
}
