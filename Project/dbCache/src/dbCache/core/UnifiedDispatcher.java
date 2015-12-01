package dbCache.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import dbCache.contract.IDispatcher;
import dbCache.models.Request;

public class UnifiedDispatcher implements IDispatcher {
	public final BlockingQueue<Request> requestQueue;
	public final AtomicInteger readyQueueCount;
	public final AtomicInteger analyzeQueueCount;
	public final AtomicInteger replyQueueCount;
	
	public UnifiedDispatcher(){
		this.requestQueue = new LinkedBlockingQueue<Request>();
		
		this.readyQueueCount = new AtomicInteger();
		this.analyzeQueueCount = new AtomicInteger();
		this.replyQueueCount = new AtomicInteger();
	}
	
	public void addRequest(Request request) throws Exception{
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
	}
	
	public Request getRequest() throws Exception{
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
	}
}
