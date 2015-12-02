package dbCache.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import dbCache.contract.IDispatcher;
import dbCache.models.Request;

public class UnifiedDispatcher implements IDispatcher {
	
	private static Logger LOGGER = Logger.getLogger(UnifiedDispatcher.class.getName());
	
	public final BlockingQueue<Request> requestQueue;
	public final AtomicInteger readyQueueCount;
	public final AtomicInteger analyzeQueueCount;
	public final AtomicInteger replyQueueCount;
	
	@Inject
	public UnifiedDispatcher(){
		this.requestQueue = new LinkedBlockingQueue<Request>(100);
		
		this.readyQueueCount = new AtomicInteger();
		this.analyzeQueueCount = new AtomicInteger();
		this.replyQueueCount = new AtomicInteger();
	}
	
	public void addRequest(Request request){
		try{
			this.requestQueue.put(request);
			
			switch(request.state){
				case New:
					this.readyQueueCount.incrementAndGet();
					break;
				case Analyze:
					this.analyzeQueueCount.incrementAndGet();
					break;
				case Reply:
					this.replyQueueCount.incrementAndGet();
					break;
				default:
					break;
			}
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public Request getRequest(){
		try{
			Request request = this.requestQueue.take();
			switch(request.state){
				case New:
					this.readyQueueCount.decrementAndGet();
					break;
				case Analyze:
					this.analyzeQueueCount.decrementAndGet();
					break;
				case Reply:
					this.replyQueueCount.decrementAndGet();
					break;
				default:
					break;
			}
			return request;
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		
		return null;
	}
}
