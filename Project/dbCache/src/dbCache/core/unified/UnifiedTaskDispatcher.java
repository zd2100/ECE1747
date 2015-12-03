package dbCache.core.unified;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import dbCache.contract.ITaskDispatcher;
import dbCache.contract.IStatistics;
import dbCache.contract.ITaskDispatcher;
import dbCache.models.Request;

public class UnifiedTaskDispatcher implements ITaskDispatcher {
	
	private static Logger LOGGER = Logger.getLogger(UnifiedTaskDispatcher.class.getName());
	
	private final IStatistics statistics;
	public final BlockingQueue<Request> requestQueue;
	
	
	@Inject
	public UnifiedTaskDispatcher(IStatistics statistics){
		this.statistics = statistics;
		this.requestQueue = new LinkedBlockingQueue<Request>(100);
	}
	
	public void addRequest(Request request){
		try{
			this.requestQueue.put(request);
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public Request getRequest(){
		try{
			Request request = this.requestQueue.take();
			return request;
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		
		return null;
	}
}
