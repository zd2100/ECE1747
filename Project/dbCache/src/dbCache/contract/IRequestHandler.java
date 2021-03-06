package dbCache.contract;

import dbCache.models.Request;

public interface IRequestHandler extends Runnable {
	
	public void start();
	public void stop();
	
	public boolean isRunning();
	
	public void handleRequest(Request request) throws Exception;
}
