package dbCache.contract;

import dbCache.models.Request;

public interface ITaskDispatcher {
	public void addRequest(Request request);
	public Request getRequest() throws InterruptedException;
}
