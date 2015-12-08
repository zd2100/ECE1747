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
	
	public synchronized void addRequest(Request request){
		try{
			switch(request.state){
			case New:
				this.requestQueue.put(request);
				this.statistics.requestQueueCount.incrementAndGet();
				break;
			case Executing:
				this.executeQueue.put(request);
				this.statistics.executeQueueCount.incrementAndGet();
				break;
			case Reply:
				this.responseQueue.put(request);
				this.statistics.replyQueueCount.incrementAndGet();
				break;
			case Done:
				this.doneQueue.put(request);
				this.statistics.doneQueueCount.incrementAndGet();
				break;
			default:
				LOGGER.log(Level.SEVERE, "Unknown Status: " + request.state);
				break;
			}
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public synchronized Request getRequest() throws InterruptedException{
		BlockingQueue<Request> queue = this.getMaxQueue();
		Request request = queue.take();
		if(queue == this.requestQueue){
			this.statistics.requestQueueCount.decrementAndGet();
		}else if(queue == this.executeQueue){
			this.statistics.executeQueueCount.decrementAndGet();
		}else if(queue == this.responseQueue){
			this.statistics.replyQueueCount.decrementAndGet();
		}else if(queue == this.doneQueue){
			this.statistics.doneQueueCount.decrementAndGet();
		}
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
}
