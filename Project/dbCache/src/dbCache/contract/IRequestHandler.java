package dbCache.contract;

import dbCache.models.Request;

public interface IRequestHandler extends Runnable {
	public void handleRequest(Request request);
}
