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
	
	private final Statistics statistics;
	private final BlockingQueue<Request> requestQueue;
	private final BlockingQueue<Request> executeQueue;
	private final BlockingQueue<Request> responseQueue;
	private final BlockingQueue<Request> doneQueue;
	
	
	@Inject
	public UnifiedTaskDispatcher(Statistics statistics){
		this.statistics = statistics;
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
				break;
			default:
				LOGGER.log(Level.SEVERE, "Unknown Status: " + request.state);
				break;
			}
			this.updateStatistics();
			
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public Request getRequest() throws InterruptedException{
		BlockingQueue<Request> queue = this.getMaxQueue();

		Request request = queue.take();
		
		this.updateStatistics();
		return request;
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
	
	private void updateStatistics(){
		synchronized(this){
			this.statistics.requestQueueCount.set(this.requestQueue.size());
			this.statistics.executeQueueCount.set(this.executeQueue.size());
			this.statistics.replyQueueCount.set(this.responseQueue.size());
			this.statistics.doneQueueCount.set(this.doneQueue.size());
		}
	}
}
