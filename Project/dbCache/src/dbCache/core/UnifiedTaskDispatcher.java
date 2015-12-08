package dbCache.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import dbCache.contract.ITaskDispatcher;
import dbCache.models.Request;
import dbCache.stats.Statistics;

public class UnifiedTaskDispatcher implements ITaskDispatcher {
	
	private static Logger LOGGER = Logger.getLogger(UnifiedTaskDispatcher.class.getName());
	
	private final BlockingQueue<Request> requestQueue;
	private final BlockingQueue<Request> executeQueue;
	private final BlockingQueue<Request> responseQueue;
	private final BlockingQueue<Request> doneQueue;
	
	
	@Inject
	public UnifiedTaskDispatcher(Statistics statistics){
		this.requestQueue = new LinkedBlockingQueue<Request>();
		this.executeQueue = new LinkedBlockingQueue<Request>();
		this.responseQueue = new LinkedBlockingQueue<Request>();
		this.doneQueue = new LinkedBlockingQueue<Request>();
	}
	
	public void addRequest(Request request){
		try{
			switch(request.state){
			case New:
				this.requestQueue.put(request);
				break;
			case Executing:
				this.executeQueue.put(request);
				break;
			case Reply:
				this.responseQueue.put(request);
				break;
			case Done:
				this.doneQueue.put(request);
			default:
				LOGGER.log(Level.SEVERE, "Unknown Status");
				break;
			}
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public Request getRequest(){
		try{
			Request request = this.getMaxQueue().take();
			return request;
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		
		return null;
	}
	
	private BlockingQueue<Request> getMaxQueue(){
		BlockingQueue<Request> max = this.requestQueue;
		if(this.executeQueue.size() > max.size()){
			max = this.executeQueue;
		}
		if(this.responseQueue.size() > max.size()){
			max = this.responseQueue;
		}
		if(this.doneQueue.size() > max.size()){
			max = this.doneQueue;
		}
		return max;
	}
}
