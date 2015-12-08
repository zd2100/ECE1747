package dbCache.stats;

import java.util.concurrent.atomic.AtomicInteger;

public class Statistics {
	public final AtomicInteger requestQueueCount;
	public final AtomicInteger executeQueueCount;
	public final AtomicInteger replyQueueCount;
	public final AtomicInteger doneQueueCount;
	public final AtomicInteger cacheHitCount;
	public final AtomicInteger cacheFetchCount;
	public final AtomicInteger delayedCacheHitCount;
	public final AtomicInteger cacheTurnoverCount;
	public final AtomicInteger handlerCount;

	public Statistics(){
		this.requestQueueCount = new AtomicInteger();
		this.executeQueueCount = new AtomicInteger();
		this.replyQueueCount = new AtomicInteger();
		this.doneQueueCount = new AtomicInteger();
		this.cacheHitCount = new AtomicInteger();
		this.cacheFetchCount = new AtomicInteger();
		this.delayedCacheHitCount = new AtomicInteger();
		this.cacheTurnoverCount = new AtomicInteger();
		this.handlerCount = new AtomicInteger();
	}
}
