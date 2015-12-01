package dbCache.contract;

import dbCache.models.Request;

public interface IDispatcher {
	public void addRequest(Request request) throws Exception;
	public Request getRequest() throws Exception;
}
